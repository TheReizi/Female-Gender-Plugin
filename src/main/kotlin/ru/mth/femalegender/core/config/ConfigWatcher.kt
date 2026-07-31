package ru.mth.femalegender.core.config

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.Plugin
import ru.mth.femalegender.core.utils.Logger
import ru.mth.femalegender.core.extensions.taskLater
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Следит за `.yml` файлами в папке плагина и её подпапках. Все ключи
 * нормализуются как относительный путь с прямыми слэшами:
 * `config.yml`, `messages.yml`, `jobs/loader.yml`, `menus/loader_delivery.yml`.
 */
class ConfigWatcher(private val plugin: Plugin) {
    private val configs = ConcurrentHashMap<String, YamlConfiguration>()
    private val ignored = ConcurrentHashMap.newKeySet<String>()
    private val lastReloadTimes = ConcurrentHashMap<String, Long>()
    private val reloadListeners = CopyOnWriteArrayList<(String) -> Unit>()

    private val watchedDirs = ConcurrentHashMap.newKeySet<Path>()

    private var watchService: WatchService? = null
    @Volatile private var isWatching = false

    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
    }

    fun registerDefaultConfig(relativePath: String) {
        val key = normalize(relativePath)
        val file = File(plugin.dataFolder, key)
        file.parentFile?.takeIf { !it.exists() }?.mkdirs()

        if (!file.exists()) {
            try {
                plugin.saveResource(key, false)
                Logger.info("Создан $key")
            } catch (e: IllegalArgumentException) {
                file.writeText("")
                Logger.info("Создан пустой $key (ресурс по умолчанию отсутствует в jar)")
            }
        }
        loadConfig(key)
    }

    fun registerDirectory(relativeDir: String) {
        val dir = File(plugin.dataFolder, normalize(relativeDir))
        if (!dir.exists()) dir.mkdirs()
        if (!dir.isDirectory) return
        loadDirectory(dir)
    }

    fun ignoreFile(relativePath: String) {
        ignored.add(normalize(relativePath))
    }

    fun getConfig(relativePath: String): YamlConfiguration {
        val key = normalize(relativePath)
        return configs[key] ?: throw IllegalArgumentException("Конфиг $key не загружен в ConfigWatcher!")
    }

    fun getConfigOrNull(relativePath: String): YamlConfiguration? = configs[normalize(relativePath)]

    fun knownConfigs(): Set<String> = configs.keys.toSet()

    fun saveConfig(relativePath: String) {
        val key = normalize(relativePath)
        val config = configs[key] ?: return
        try {
            val file = File(plugin.dataFolder, key)
            file.parentFile?.takeIf { !it.exists() }?.mkdirs()
            config.save(file)
        } catch (e: IOException) {
            Logger.error("Не удалось сохранить $key", e)
        }
    }

    fun loadAllConfigs() {
        loadDirectory(plugin.dataFolder)
    }

    private fun loadDirectory(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                loadDirectory(file)
                continue
            }
            if (!file.name.endsWith(".yml")) continue
            val rel = relativeKey(file)
            if (ignored.contains(rel)) continue
            loadConfig(rel)
        }
    }

    private fun loadConfig(relativePath: String) {
        val key = normalize(relativePath)
        val file = File(plugin.dataFolder, key)
        if (!file.exists()) return
        try {
            val config = YamlConfiguration()
            config.load(file)
            configs[key] = config
        } catch (e: Exception) {
            Logger.error("ОШИБКА YAML: Не удалось загрузить $key. Проверьте отступы и кавычки.", e)
            configs.putIfAbsent(key, YamlConfiguration())
        }
    }

    fun addReloadListener(listener: (String) -> Unit) {
        reloadListeners.add(listener)
    }

    fun startWatcher() {
        if (isWatching) return

        try {
            val service = FileSystems.getDefault().newWatchService()
            watchService = service
            val baseDirs = mutableSetOf<Path>(plugin.dataFolder.toPath())
            for (key in configs.keys) {
                val parent = File(plugin.dataFolder, key).parentFile?.toPath() ?: continue
                baseDirs.add(parent)
            }
            for (dir in baseDirs) {
                if (!dir.toFile().exists()) dir.toFile().mkdirs()
                dir.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY)
                watchedDirs.add(dir)
            }
            isWatching = true

            Thread({
                while (isWatching) {
                    try {
                        val key: WatchKey = watchService!!.take()
                        val watchable = key.watchable() as? Path ?: run { key.reset(); continue }
                        for (event in key.pollEvents()) {
                            val ctx = event.context() as? Path ?: continue
                            val full = watchable.resolve(ctx).toFile()
                            if (!full.name.endsWith(".yml")) continue
                            val rel = relativeKey(full)
                            if (ignored.contains(rel)) continue

                            val now = System.currentTimeMillis()
                            val lastReload = lastReloadTimes[rel] ?: 0L
                            if (now - lastReload < 1000) continue
                            lastReloadTimes[rel] = now

                            safeTaskLater(10L) { reloadFile(rel, full) }
                        }
                        if (!key.reset()) break
                    } catch (_: InterruptedException) {
                        break
                    } catch (_: ClosedWatchServiceException) {
                        break
                    }
                }
            }, "${plugin.name}-ConfigWatcher").start()

        } catch (e: IOException) {
            Logger.warn("Не удалось настроить наблюдатель файлов: ${e.message}")
        }
    }

    private fun reloadFile(rel: String, file: File) {
        if (!file.exists()) return
        if (file.length() == 0L) {
            safeTaskLater(10L) { if (file.length() > 0) reloadFile(rel, file) }
            return
        }
        try {
            val newConfig = YamlConfiguration()
            newConfig.load(file)
            configs[rel] = newConfig
            reloadListeners.forEach { it.invoke(rel) }
        } catch (e: Exception) {
            Logger.error("⚠ ОШИБКА YAML в $rel! Изменения отклонены. Проверьте отступы (без TAB)!")
        }
    }

    /**
     * Планирует задачу, только если плагин ещё включён. Paper выставляет
     * plugin.isEnabled = false до вызова onDisable(), поэтому поток вотчера
     * может успеть получить файловое событие и попытаться запланировать
     * задачу ровно в момент отключения — это не ошибка, просто игнорируем.
     */
    private fun safeTaskLater(delay: Long, block: () -> Unit) {
        if (!isWatching || !plugin.isEnabled) return
        try {
            plugin.taskLater(delay, block)
        } catch (_: IllegalPluginAccessException) {
        }
    }

    fun shutdown() {
        isWatching = false
        try {
            watchService?.close()
        } catch (e: IOException) {
            Logger.warn("Ошибка при закрытии WatchService: ${e.message}")
        }
    }

    private fun relativeKey(file: File): String {
        val rel = plugin.dataFolder.toPath().relativize(file.toPath()).toString()
        return rel.replace('\\', '/')
    }

    private fun normalize(path: String): String = path.replace('\\', '/').trimStart('/')
}
