package ru.mth.femalegender.core.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object LicenseChecker {
    private const val LICENSE_URL = "http://31.128.36.239/license-checker.php"

    fun verify(plugin: JavaPlugin): Boolean {
        Logger.info("Проверка лицензии (соединение с сервером)...")

        try {
            val client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build()

            val request = HttpRequest.newBuilder()
                .uri(URI.create(LICENSE_URL))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200 && response.body().contains("\"status\":\"success\"")) {
                Logger.info("<#00FF00>Лицензия подтверждена!</#00FF00>")
                return true
            } else {
                Logger.error("=========================================")
                Logger.error(" ОШИБКА ЛИЦЕНЗИИ: Ваш IP-адрес не зарегистрирован!")
                Logger.error(" Купите плагин или обратитесь к разработчику.")
                Logger.error("=========================================")
                Bukkit.getPluginManager().disablePlugin(plugin)
                return false
            }

        } catch (e: Exception) {
            Logger.error("=========================================")
            Logger.error(" ОШИБКА СЕРВЕРА ЛИЦЕНЗИЙ: Не удалось связаться с сервером проверки.")
            Logger.error(" Плагин будет отключен. Ошибка: ${e.message}")
            Logger.error("=========================================")
            Bukkit.getPluginManager().disablePlugin(plugin)
            return false
        }
    }
}
