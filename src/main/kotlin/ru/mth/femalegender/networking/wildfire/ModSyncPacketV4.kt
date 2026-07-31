package ru.mth.femalegender.networking.wildfire

object ModSyncPacketV4 : AbstractModSyncPacket(hasArmorPhysics = false, hasVoicePitch = true) {
    override val version = 4
    override val modRange = "4.0.1 - ?.?.?"
}
