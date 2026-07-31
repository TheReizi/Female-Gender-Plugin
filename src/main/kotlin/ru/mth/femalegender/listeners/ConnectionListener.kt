package ru.mth.femalegender.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.mth.femalegender.Main
import ru.mth.femalegender.core.utils.Logger

class ConnectionListener(private val plugin: Main) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        Logger.info("Syncing ${player.name}")
        // Ресинкаем ВСЕХ, а не только вошедшего: у вошедшего может быть принудительный
        // гендер (FemaleGenderAPI), но сам он его клиенту никогда не пришлёт (мод не
        // установлен) — значит уже сидящие на сервере игроки иначе никогда не узнают
        // о его гендере, пока кто-то другой не вызовет sync().
        plugin.networkManager.sync(plugin.server.onlinePlayers)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        Logger.debug("Removing ${player.name}")
        plugin.userManager.users.remove(player.uniqueId)
    }
}
