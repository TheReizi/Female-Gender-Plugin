package ru.mth.femalegender.networking.wildfire

object ModSyncPacketV3 : AbstractModSyncPacket(hasArmorPhysics = false, hasVoicePitch = false) {
    override val version = 3
    override val modRange = "3.1.0 - 4.0.0"
}
