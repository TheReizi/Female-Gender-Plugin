package ru.mth.femalegender.networking

import org.bukkit.entity.Player
import ru.mth.femalegender.Main
import ru.mth.femalegender.api.FemaleGenderAPI
import ru.mth.femalegender.core.utils.Logger
import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.networking.wildfire.ModSyncPacket
import ru.mth.femalegender.networking.wildfire.ModSyncPacketV1
import ru.mth.femalegender.networking.wildfire.ModSyncPacketV2
import ru.mth.femalegender.networking.wildfire.ModSyncPacketV3
import ru.mth.femalegender.networking.wildfire.ModSyncPacketV4
import ru.mth.femalegender.networking.wildfire.ModSyncPacketV5
import ru.mth.femalegender.wildfire.ModConstants
import ru.mth.femalegender.wildfire.ModUser
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class NetworkManager(private val plugin: Main) {
    // defaultFormat/autoDetect are written from ConfigWatcher's background watcher thread
    // (see reloadProtocol) and read from whatever thread calls sync()/deserializeUser() —
    // on Folia that can be any region thread, so both need to be visible across threads.
    @Volatile private lateinit var defaultFormat: ModSyncPacket

    // When auto-detect is on (default), each player's protocol is inferred from their own
    // inbound packets (see detectAndParse) and defaultFormat is only a fallback for recipients
    // we haven't heard from yet. Server owners can turn it off in config.yml to force
    // defaultFormat for everyone, as an escape hatch if detection ever misbehaves.
    @Volatile private var autoDetect = true
    private val playerProtocols = ConcurrentHashMap<UUID, ModSyncPacket>()

    // A modern (5.x) Fabric client registers its custom-payload receiver before
    // sending wildfire_gender:serverbound/hello. PlayerJoinEvent happens earlier
    // than that registration, so it is not a safe moment for the first sync.
    private val readyRecipients = ConcurrentHashMap.newKeySet<UUID>()

    fun init(): Boolean {
        val format = resolveFormat() ?: return false
        defaultFormat = format
        autoDetect = resolveAutoDetect()
        logProtocolMode()
        return true
    }

    fun reloadProtocol() {
        val format = resolveFormat()
        if (format == null) {
            Logger.error("INVALID PROTOCOL in config.yml, keeping default protocol ${defaultFormat.version}")
            return
        }
        val newAutoDetect = resolveAutoDetect()
        val changed = format.version != defaultFormat.version || newAutoDetect != autoDetect
        defaultFormat = format
        autoDetect = newAutoDetect
        if (changed) {
            Logger.info("Reloaded config.yml:")
            logProtocolMode()
        }
    }

    private fun logProtocolMode() {
        if (autoDetect) {
            Logger.info("Auto-detecting each player's protocol; default/fallback is ${defaultFormat.version} (${defaultFormat.modRange})")
        } else {
            Logger.info("Auto-detect disabled: forcing protocol ${defaultFormat.version} (${defaultFormat.modRange}) for everyone")
        }
    }

    private fun resolveFormat(): ModSyncPacket? {
        val protocolVersion = plugin.configWatcher.getConfig("config.yml").getInt("mod.protocol", -1)
        val resolvedVersion = if (protocolVersion == -1) PACKET_FORMATS.keys.max() else protocolVersion
        return PACKET_FORMATS[resolvedVersion]
    }

    private fun resolveAutoDetect(): Boolean =
        plugin.configWatcher.getConfig("config.yml").getBoolean("mod.auto-detect", true)

    fun forget(playerId: UUID) {
        playerProtocols.remove(playerId)
        readyRecipients.remove(playerId)
    }

    /** Called after the client has registered its payload channels and sent hello. */
    fun markReady(player: Player) {
        readyRecipients += player.uniqueId
        Logger.debug("${player.name} is ready for FemaleGender sync payloads")
        sync(plugin.server.onlinePlayers)
    }

    fun sync(audience: Collection<Player>) {
        val targetIds = plugin.userManager.users.keys + FemaleGenderAPI.forcedTargets()

        for (targetId in targetIds) {
            val realUser = plugin.userManager.users[targetId]
            val forced = FemaleGenderAPI.getForcedGender(targetId)
            val baseUser = when {
                realUser != null && forced != null -> realUser.copy(
                    configuration = realUser.configuration.copy(
                        generalOptions = realUser.configuration.generalOptions.copy(genderIdentity = forced.identity)
                    )
                )
                realUser != null -> realUser
                forced != null -> ModUser(targetId, forced.fallback)
                else -> continue
            }

            val defaultDataByFormat = HashMap<ModSyncPacket, Pair<ByteArray, ByteArray>>()
            for (recipient in audience) {
                // The 5.x client deliberately rejects its own ClientboundSyncPacket.
                // Sending it is not just noisy: it hid the fact that the first useful
                // recipient had not been ready yet.
                if (recipient.uniqueId == targetId) continue

                // Modern clients announce readiness with hello after their receiver is
                // registered. Older clients did not have this handshake, so retain the
                // fallback path once they have sent their own gender payload.
                if (!readyRecipients.contains(recipient.uniqueId) && !playerProtocols.containsKey(recipient.uniqueId)) continue

                val format = if (autoDetect) playerProtocols[recipient.uniqueId] ?: defaultFormat else defaultFormat
                val override = FemaleGenderAPI.getOverride(targetId, recipient.uniqueId)

                val (fabricData, forgeData) = if (override != null) {
                    val overriddenUser = baseUser.copy(configuration = override)
                    serializeUser(overriddenUser, format, forge = false) to serializeUser(overriddenUser, format, forge = true)
                } else {
                    defaultDataByFormat.getOrPut(format) {
                        serializeUser(baseUser, format, forge = false) to serializeUser(baseUser, format, forge = true)
                    }
                }

                if (fabricData.isNotEmpty()) sendData(recipient, ModConstants.SYNC, fabricData)
                if (forgeData.isNotEmpty()) sendData(recipient, ModConstants.FORGE, forgeData)
            }
        }
    }

    fun deserializeUser(data: ByteArray, forge: Boolean): ModUser? {
        if (!autoDetect) {
            return try {
                CraftInputStream.ofBytes(data).use { input ->
                    if (forge) input.readByte()
                    defaultFormat.read(input)
                }
            } catch (ex: IOException) {
                Logger.warn("Could not deserialize user (forge=$forge): ${ex.message}")
                null
            }
        }

        val (format, user) = detectAndParse(data, forge) ?: run {
            Logger.warn("Could not deserialize user (forge=$forge): no known protocol matches this packet")
            return null
        }
        playerProtocols[user.userId] = format
        Logger.debug("Detected sync protocol V${format.version} for ${user.userId} (forge=$forge)")
        return user
    }

    // No format is self-describing on the wire, so we try each known one and accept the first
    // that both parses without error AND consumes the buffer exactly. V2/V3/V4 have distinct
    // fixed lengths and V5 always adds its UV section on top, so a partial/wrong-format match
    // always either throws or leaves leftover bytes — there's no ambiguity between them.
    private fun detectAndParse(data: ByteArray, forge: Boolean): Pair<ModSyncPacket, ModUser>? {
        for (format in PACKET_FORMATS.values) {
            val user = runCatching {
                CraftInputStream.ofBytes(data).use { input ->
                    if (forge) input.readByte()
                    val parsed = format.read(input)
                    if (input.available() != 0) error("trailing bytes")
                    parsed
                }
            }.getOrNull() ?: continue
            return format to user
        }
        return null
    }

    private fun serializeUser(user: ModUser, format: ModSyncPacket, forge: Boolean): ByteArray {
        return try {
            ByteArrayOutputStream().use { payload ->
                CraftOutputStream(payload).use { output ->
                    if (forge) output.writeByte(1)
                    format.write(user, output)
                }
                payload.toByteArray()
            }
        } catch (ex: IOException) {
            Logger.warn("Could not serialize user (forge=$forge): ${ex.message}")
            ByteArray(0)
        }
    }

    // Routed through the target's own entity scheduler so this is always dispatched on the
    // thread that actually owns them — required on Folia (sync() may be called from a
    // different region, or even off the main thread via FemaleGenderAPI), a no-op hop on Paper.
    private fun sendData(target: Player, channel: String, data: ByteArray) {
        target.scheduler.run(plugin, { target.sendPluginMessage(plugin, channel, data) }, null)
    }

    companion object {
        private val PACKET_FORMATS: Map<Int, ModSyncPacket> = mapOf(
            1 to ModSyncPacketV1,
            2 to ModSyncPacketV2,
            3 to ModSyncPacketV3,
            4 to ModSyncPacketV4,
            5 to ModSyncPacketV5
        )
    }
}
