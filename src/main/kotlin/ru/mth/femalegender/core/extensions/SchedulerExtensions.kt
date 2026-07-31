package ru.mth.femalegender.core.extensions

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

// Синхронная задача (глобально)
fun Plugin.task(block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().execute(this) { block() }

// Синхронная с задержкой (ticks)
fun Plugin.taskLater(delay: Long, block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().runDelayed(this, { _ -> block() }, delay)

// Синхронный таймер
fun Plugin.taskTimer(delay: Long, period: Long, block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, { _ -> block() }, delay, period)

// Асинхронная задача (сразу)
fun Plugin.taskAsync(block: () -> Unit) =
    Bukkit.getAsyncScheduler().runNow(this) { _ -> block() }

// Асинхронная с задержкой (миллисекунды!)
fun Plugin.taskLaterAsync(delay: Long, block: () -> Unit) =
    Bukkit.getAsyncScheduler().runDelayed(this, { _ -> block() }, delay, TimeUnit.MILLISECONDS)

// Асинхронный таймер (миллисекунды!)
fun Plugin.taskTimerAsync(delay: Long, period: Long, block: () -> Unit) =
    Bukkit.getAsyncScheduler().runAtFixedRate(this, { _ -> block() }, delay, period, TimeUnit.MILLISECONDS)
