package ru.mth.femalegender.core.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import ru.mth.femalegender.core.utils.ColorConverter

class ConfigManager(plugin: Plugin) {
    val watcher = ConfigWatcher(plugin)
    private val miniMessage = MiniMessage.miniMessage()

    val mainConfig: YamlConfiguration
        get() = watcher.getConfig("config.yml")

    val messagesConfig: YamlConfiguration
        get() = watcher.getConfig("messages.yml")

    fun setup() {
        watcher.registerDefaultConfig("config.yml")
        watcher.registerDefaultConfig("messages.yml")

        watcher.loadAllConfigs()
        initializeMessagesConfig()

        watcher.addReloadListener { fileName ->
            when (fileName) {
                "config.yml"   -> { }
                "messages.yml" -> { }
            }
        }
        watcher.startWatcher()
    }

    fun shutdown() {
        watcher.shutdown()
    }

    private fun initializeMessagesConfig() {
        var changed = false
        val msgConfig = watcher.getConfig("messages.yml")

        for (key in MessageKey.entries) {
            if (!msgConfig.contains(key.key)) {
                msgConfig.set(key.key, key.defaultMessage)
                changed = true
            }
        }

        if (changed) {
            watcher.saveConfig("messages.yml")
        }
    }

    fun getMessage(key: MessageKey, placeholders: Map<String, Any> = emptyMap()): Component {
        var message: String = if (messagesConfig.isList(key.key)) {
            messagesConfig.getStringList(key.key).joinToString("\n")
        } else {
            messagesConfig.getString(key.key) ?: key.defaultMessage
        }

        placeholders.forEach { (placeholder, value) ->
            message = message.replace("{$placeholder}", value.toString())
        }

        message = message.replace("<br>", "\n")
        val mmString = ColorConverter.convertLegacyToMiniMessage(message)
        return miniMessage.deserialize(mmString)
    }
}
