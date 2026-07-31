package ru.mth.femalegender

import org.bukkit.plugin.java.JavaPlugin
import ru.mth.femalegender.core.utils.Logger
import ru.mth.femalegender.listeners.ConnectionListener
import ru.mth.femalegender.listeners.HelloListener
import ru.mth.femalegender.listeners.ModPayloadListener
import ru.mth.femalegender.networking.NetworkManager
import ru.mth.femalegender.wildfire.ModConstants
import ru.mth.femalegender.wildfire.UserManager

class Main : JavaPlugin() {

    companion object {
        /** Для FemaleGenderAPI — доступ к networkManager, чтобы triggerить немедленный ресинк. */
        lateinit var instance: Main
            private set
    }

    val userManager = UserManager()
    val networkManager = NetworkManager(this)

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        if (!networkManager.init()) {
            Logger.error("INVALID PROTOCOL, DISABLING SELF.")
            server.pluginManager.disablePlugin(this)
        }

        registerEventListeners()
        registerModListeners()
    }

    override fun onDisable() {
        server.messenger.unregisterIncomingPluginChannel(this)
        server.messenger.unregisterOutgoingPluginChannel(this)
    }

    private fun registerEventListeners() {
        server.pluginManager.registerEvents(ConnectionListener(this), this)
    }

    private fun registerModListeners() {
        val payloadListener = ModPayloadListener(this)
        server.messenger.registerIncomingPluginChannel(this, ModConstants.SEND_GENDER_INFO, payloadListener)
        server.messenger.registerOutgoingPluginChannel(this, ModConstants.SYNC)
        server.messenger.registerIncomingPluginChannel(this, ModConstants.FORGE, payloadListener)
        server.messenger.registerOutgoingPluginChannel(this, ModConstants.FORGE)

        val helloListener = HelloListener(this)
        server.messenger.registerIncomingPluginChannel(this, ModConstants.HELLO_SERVERBOUND, helloListener)
        server.messenger.registerOutgoingPluginChannel(this, ModConstants.HELLO_CLIENTBOUND)
    }
}
