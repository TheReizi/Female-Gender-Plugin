package ru.mth.femalegender.networking.wildfire

import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser

object ModSyncPacketV1 : ModSyncPacket {
    override val version = 1
    override val modRange = "0.0.1 - 2.8.0"

    override fun read(input: CraftInputStream): ModUser = throw UnsupportedOperationException("Not implemented!")
    override fun write(user: ModUser, output: CraftOutputStream): Unit = throw UnsupportedOperationException("Not implemented!")
}
