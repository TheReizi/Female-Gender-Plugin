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

class NetworkManager(private val plugin: Main) {
    private lateinit var packetFormat: ModSyncPacket

    fun init(): Boolean {
        val protocolVersion = plugin.config.getInt("mod.protocol", -1)
        val resolvedVersion = if (protocolVersion == -1) PACKET_FORMATS.keys.max() else protocolVersion
        val format = PACKET_FORMATS[resolvedVersion] ?: return false
        packetFormat = format
        Logger.info("Using protocol ${format.version} for mod version(s) ${format.modRange}")
        return true
    }

    fun sync(audience: Collection<Player>) {
        // Цели = все, кто когда-либо прислал свои данные с клиента, ПЛЮС все, у кого есть
        // принудительный гендер (FemaleGenderAPI.setForcedGender) — включая игроков без
        // мода вообще, которые сами никогда бы сюда не попали (см. baseUser ниже).
        val targetIds = plugin.userManager.users.keys + FemaleGenderAPI.forcedTargets()

        for (targetId in targetIds) {
            val realUser = plugin.userManager.users[targetId]
            val forced = FemaleGenderAPI.getForcedGender(targetId)

            // Принудительный гендер лочит ТОЛЬКО generalOptions.genderIdentity поверх реальных
            // данных игрока (размер груди/физика остаются его собственными). Если реальных
            // данных нет вообще (мод не установлен) — целиком используется fallback-конфиг.
            val baseUser = when {
                realUser != null && forced != null -> realUser.copy(
                    configuration = realUser.configuration.copy(
                        generalOptions = realUser.configuration.generalOptions.copy(genderIdentity = forced.identity)
                    )
                )
                realUser != null -> realUser
                forced != null -> ModUser(targetId, forced.fallback)
                else -> continue // недостижимо: targetId взят из одного из этих двух источников
            }

            val defaultFabricData = serializeUser(baseUser, forge = false)
            val defaultForgeData = serializeUser(baseUser, forge = true)
            for (recipient in audience) {
                val override = FemaleGenderAPI.getOverride(targetId, recipient.uniqueId)

                val (fabricData, forgeData) = if (override != null) {
                    val overriddenUser = baseUser.copy(configuration = override)
                    serializeUser(overriddenUser, forge = false) to serializeUser(overriddenUser, forge = true)
                } else {
                    defaultFabricData to defaultForgeData
                }

                if (fabricData.isNotEmpty()) sendData(recipient, ModConstants.SYNC, fabricData)
                if (forgeData.isNotEmpty()) sendData(recipient, ModConstants.FORGE, forgeData)
            }
        }
    }

    fun deserializeUser(data: ByteArray, forge: Boolean): ModUser? {
        return try {
            CraftInputStream.ofBytes(data).use { input ->
                if (forge) input.readByte()
                packetFormat.read(input)
            }
        } catch (ex: IOException) {
            Logger.warn("Could not deserialize user (forge=$forge): ${ex.message}")
            null
        }
    }

    private fun serializeUser(user: ModUser, forge: Boolean): ByteArray {
        return try {
            ByteArrayOutputStream().use { payload ->
                CraftOutputStream(payload).use { output ->
                    if (forge) output.writeByte(1)
                    packetFormat.write(user, output)
                }
                payload.toByteArray()
            }
        } catch (ex: IOException) {
            Logger.warn("Could not serialize user (forge=$forge): ${ex.message}")
            ByteArray(0)
        }
    }

    private fun sendData(target: Player, channel: String, data: ByteArray) {
        target.sendPluginMessage(plugin, channel, data)
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
