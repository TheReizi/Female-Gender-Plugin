package ru.mth.femalegender.networking.wildfire

object ModSyncPacketV2 : AbstractModSyncPacket(hasArmorPhysics = true, hasVoicePitch = false) {
    override val version = 2
    override val modRange = "2.8.1 - 3.0.1"
}
