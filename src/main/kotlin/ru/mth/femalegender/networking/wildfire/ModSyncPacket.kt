package ru.mth.femalegender.networking.wildfire

import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser

interface ModSyncPacket {
    val version: Int
    val modRange: String

    fun read(input: CraftInputStream): ModUser
    fun write(user: ModUser, output: CraftOutputStream)
}
