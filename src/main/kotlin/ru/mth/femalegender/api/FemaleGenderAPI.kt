package ru.mth.femalegender.api

import ru.mth.femalegender.Main
import ru.mth.femalegender.wildfire.setup.GenderIdentities
import ru.mth.femalegender.wildfire.setup.ModConfiguration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Публичный API для сторонних плагинов. Позволяет подменять то, каким видит
 * игрока (target) конкретный зритель (viewer), не трогая собственные данные
 * target'а — они по-прежнему приходят с его клиента и хранятся как есть.
 * Override просто перекрывает их при рассылке конкретному viewer'у.
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

    // ── Принудительный гендер (для ВСЕХ зрителей разом) ──────────────────────

    /**
     * @param identity гендер, авторитетно закреплённый внешним источником (напр. RP-анкетой)
     * @param fallback конфигурация целиком — используется, только если у target'а вообще
     *   нет собственных данных мода (никогда не присылал их — мод не установлен/не запущен).
     *   Если данные есть — подменяется только [ModConfiguration.generalOptions]`.genderIdentity`,
     *   остальное (размер груди, физика и т.п.) остаётся собственным игрока.
     */
    data class ForcedGender(val identity: GenderIdentities, val fallback: ModConfiguration)

    private val forcedGenders = ConcurrentHashMap<UUID, ForcedGender>()

    /** Закрепляет гендер за [target] для всех зрителей и немедленно пере-рассылает. См. [ForcedGender]. */
    fun setForcedGender(target: UUID, identity: GenderIdentities, fallback: ModConfiguration) {
        forcedGenders[target] = ForcedGender(identity, fallback)
        resync()
    }

    /** Снимает принудительный гендер с [target] (напр. анкета сброшена/гендер ещё не выбран). */
    fun clearForcedGender(target: UUID) {
        if (forcedGenders.remove(target) != null) resync()
    }

    fun getForcedGender(target: UUID): ForcedGender? = forcedGenders[target]

    /** UUID всех игроков с принудительным гендером — читается [ru.mth.femalegender.networking.NetworkManager.sync]. */
    fun forcedTargets(): Set<UUID> = forcedGenders.keys

    /** Best-effort: если плагин ещё/уже не enabled (race при старте/остановке) — просто не ресинкаем. */
    private fun resync() {
        runCatching {
            val plugin = Main.instance
            plugin.networkManager.sync(plugin.server.onlinePlayers)
        }
    }
}
