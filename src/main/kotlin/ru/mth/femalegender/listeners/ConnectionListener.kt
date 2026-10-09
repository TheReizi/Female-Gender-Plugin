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
        Logger.info("Waiting for ${player.name}'s FemaleGender hello before syncing")
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        Logger.debug("Removing ${player.name}")
        plugin.userManager.users.remove(player.uniqueId)
        plugin.networkManager.forget(player.uniqueId)
    }
}
