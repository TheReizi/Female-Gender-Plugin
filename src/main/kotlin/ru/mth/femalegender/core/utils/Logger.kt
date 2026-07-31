package ru.mth.femalegender.core.utils

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import java.io.PrintWriter
import java.io.StringWriter

object Logger {
    private val mm = MiniMessage.miniMessage()
    private const val PREFIX = "<#FF6600>[FemaleGenderPlugin]</#FF6600>"

    fun info(msg: String)  = log("<#0FF000>", msg)
    fun warn(msg: String)  = log("<#FFFF00>", msg)
    fun error(msg: String) = log("<#FF5555>", msg)

    fun error(msg: String, e: Throwable) {
        error(msg)
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        log("<#FF5555>", sw.toString())
    }

    fun debug(msg: String) = log("<#005DFF>", msg)

    private fun log(color: String, msg: String) {
        Bukkit.getConsoleSender().sendMessage(mm.deserialize("$PREFIX $color$msg"))
    }
}
