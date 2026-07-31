package ru.mth.femalegender.listeners

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import ru.mth.femalegender.Main
import ru.mth.femalegender.core.utils.Logger
import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModConstants
import java.io.ByteArrayOutputStream

class HelloListener(private val plugin: Main) : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != ModConstants.HELLO_SERVERBOUND) return

        val version = CraftInputStream.ofBytes(message).use { it.readVarInt() }
        Logger.debug("Received hello from ${player.name} using sync protocol version $version")

        val reply = ByteArrayOutputStream().use { payload ->
            CraftOutputStream(payload).use { it.writeVarInt(ModConstants.HELLO_VERSION) }
            payload.toByteArray()
        }
        player.sendPluginMessage(plugin, ModConstants.HELLO_CLIENTBOUND, reply)
    }
}
