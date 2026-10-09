package ru.mth.femalegender.listeners

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import ru.mth.femalegender.Main
import ru.mth.femalegender.core.utils.Logger
import ru.mth.femalegender.wildfire.ModConstants

class ModPayloadListener(private val plugin: Main) : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != ModConstants.SEND_GENDER_INFO && channel != ModConstants.FORGE) return
        val user = plugin.networkManager.deserializeUser(message, channel == ModConstants.FORGE) ?: return

        if (player.uniqueId != user.userId) {
            Logger.warn("Unauthorized access attempt by ${player.name} for ${user.userId}")
            return
        }

        plugin.userManager.users[user.userId] = user
        Logger.debug("Stored ${player.name} as ${user.configuration.generalOptions.genderIdentity.name}")
        // A client which already sent its payload is definitely able to receive
        // a reply, even if it is a legacy build without the hello handshake.
        plugin.networkManager.markReady(player)
    }
}
