package ru.mth.femalegender.api

import ru.mth.femalegender.Main
import ru.mth.femalegender.wildfire.setup.GenderIdentities
import ru.mth.femalegender.wildfire.setup.ModConfiguration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Public API for third-party plugins. Lets you change how a player (target)
 * is seen by a specific viewer, without touching the target's own data — it
 * still arrives from their client and is stored as-is. An override merely
 * shadows it when broadcasting to that particular viewer.
 */
object FemaleGenderAPI {
    private val overrides = ConcurrentHashMap<UUID, MutableMap<UUID, ModConfiguration>>()

    fun setOverride(target: UUID, viewer: UUID, configuration: ModConfiguration) {
        overrides.computeIfAbsent(target) { ConcurrentHashMap() }[viewer] = configuration
    }

    fun clearOverride(target: UUID, viewer: UUID) {
        overrides[target]?.remove(viewer)
    }

    fun clearOverridesFor(target: UUID) {
        overrides.remove(target)
    }

    fun getOverride(target: UUID, viewer: UUID): ModConfiguration? = overrides[target]?.get(viewer)

    // ── Forced gender (applies to ALL viewers at once) ───────────────────────

    /**
     * @param identity gender authoritatively assigned by an external source (e.g. an RP application)
     * @param fallback the full configuration — used only if the target has no mod data of their
     *   own at all (never sent any — mod not installed/not running). If they do have data, only
     *   [ModConfiguration.generalOptions]`.genderIdentity` is overridden; everything else
     *   (bust size, physics, etc.) remains the player's own.
     */
    data class ForcedGender(val identity: GenderIdentities, val fallback: ModConfiguration)

    private val forcedGenders = ConcurrentHashMap<UUID, ForcedGender>()

    /** Locks [target]'s gender for all viewers and immediately re-broadcasts. See [ForcedGender]. */
    fun setForcedGender(target: UUID, identity: GenderIdentities, fallback: ModConfiguration) {
        forcedGenders[target] = ForcedGender(identity, fallback)
        resync()
    }

    /** Removes the forced gender from [target] (e.g. application reset/gender not chosen yet). */
    fun clearForcedGender(target: UUID) {
        if (forcedGenders.remove(target) != null) resync()
    }

    fun getForcedGender(target: UUID): ForcedGender? = forcedGenders[target]

    /** UUIDs of all players with a forced gender — read by [ru.mth.femalegender.networking.NetworkManager.sync]. */
    fun forcedTargets(): Set<UUID> = forcedGenders.keys

    /** Best-effort: if the plugin isn't enabled yet/anymore (race during startup/shutdown), just skip the resync. */
    private fun resync() {
        runCatching {
            val plugin = Main.instance
            plugin.networkManager.sync(plugin.server.onlinePlayers)
        }
    }
}
