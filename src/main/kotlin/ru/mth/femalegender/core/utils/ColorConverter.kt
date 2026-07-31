package ru.mth.femalegender.core.utils

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

object ColorConverter {
    private val HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})")
    private val MM_HEX_PATTERN = Regex("&#([A-Fa-f0-9]{6})")
    private val MM_LEGACY_PATTERN = Regex("&([0-9a-fk-orA-FK-OR])")

    private val legacyToMiniMessageMap = mapOf(
        '0' to "black", '1' to "dark_blue", '2' to "dark_green", '3' to "dark_aqua",
        '4' to "dark_red", '5' to "dark_purple", '6' to "gold", '7' to "gray",
        '8' to "dark_gray", '9' to "blue", 'a' to "green", 'b' to "aqua",
        'c' to "red", 'd' to "light_purple", 'e' to "yellow", 'f' to "white",
        'k' to "obfuscated", 'l' to "bold", 'm' to "strikethrough", 'n' to "underlined",
        'o' to "italic", 'r' to "reset"
    )

    fun convertLegacyToMiniMessage(text: String): String {
        var translatedText = text

        translatedText = translatedText.replace(MM_HEX_PATTERN) { match ->
            "<#${match.groupValues[1]}>"
        }

        translatedText = translatedText.replace(MM_LEGACY_PATTERN) { match ->
            val code = match.groupValues[1].lowercase()[0]
            val tagName = legacyToMiniMessageMap[code]
            if (tagName != null) "<$tagName>" else match.value
        }

        return translatedText
    }

    fun colorize(text: String): String {
        var translatedText = text
        var matcher = HEX_PATTERN.matcher(translatedText)
        while (matcher.find()) {
            val color = matcher.group(1)
            translatedText = translatedText.replace(
                matcher.group(),
                ChatColor.of("#$color").toString()
            )
            matcher = HEX_PATTERN.matcher(translatedText)
        }
        return ChatColor.translateAlternateColorCodes('&', translatedText)
    }

    fun colorize(list: List<String>): List<String> { return list.map { colorize(it) } }
}
