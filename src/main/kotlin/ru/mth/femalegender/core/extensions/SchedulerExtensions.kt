package ru.mth.femalegender.core.extensions

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

fun Plugin.task(block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().execute(this) { block() }

fun Plugin.taskLater(delay: Long, block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().runDelayed(this, { _ -> block() }, delay)

fun Plugin.taskTimer(delay: Long, period: Long, block: () -> Unit) =
    Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, { _ -> block() }, delay, period)

fun Plugin.taskAsync(block: () -> Unit) =
    Bukkit.getAsyncScheduler().runNow(this) { _ -> block() }

// delay is in milliseconds here, unlike the sync taskLater above where it's ticks
fun Plugin.taskLaterAsync(delay: Long, block: () -> Unit) =
    Bukkit.getAsyncScheduler().runDelayed(this, { _ -> block() }, delay, TimeUnit.MILLISECONDS)

// delay/period are in milliseconds here, unlike the sync taskTimer above where they're ticks
fun Plugin.taskTimerAsync(delay: Long, period: Long, block: () -> Unit) =
    Bukkit.getAsyncScheduler().runAtFixedRate(this, { _ -> block() }, delay, period, TimeUnit.MILLISECONDS)
