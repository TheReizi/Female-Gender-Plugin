package ru.mth.femalegender.networking.wildfire

import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser
import ru.mth.femalegender.wildfire.setup.BreastOptions
import ru.mth.femalegender.wildfire.setup.GenderIdentities
import ru.mth.femalegender.wildfire.setup.GeneralOptions
import ru.mth.femalegender.wildfire.setup.ModConfiguration
import ru.mth.femalegender.wildfire.setup.PhysicsOptions

/**
 * V2 добавил armorPhysics, V4 заменил его на voicePitch — оба поля никогда
 * не сосуществуют, поэтому порядок чтения/записи общий для всех версий.
 * V5 (см. ModSyncPacketV5) переиспользует этот же порядок и лишь дописывает
 * секцию UV-layout поверх него.
 */
abstract class AbstractModSyncPacket(
    private val hasArmorPhysics: Boolean,
    private val hasVoicePitch: Boolean
) : ModSyncPacket {

    override fun read(input: CraftInputStream): ModUser {
        val userId = input.readUUID()
        val genderIdentity = input.readEnum<GenderIdentities>()
        val bustSize = input.readFloat()
        val hurtSounds = input.readBoolean()
        val voicePitch = if (hasVoicePitch) input.readFloat() else 0f
        val breastPhysics = input.readBoolean()
        val armorPhysics = if (hasArmorPhysics) input.readBoolean() else false
        val showInArmor = input.readBoolean()
        val buoyancy = input.readFloat()
        val floppiness = input.readFloat()
        val xOffset = input.readFloat()
        val yOffset = input.readFloat()
        val zOffset = input.readFloat()
        val uniBoob = input.readBoolean()
        val cleavage = input.readFloat()

        return ModUser(
            userId,
            ModConfiguration(
                GeneralOptions(genderIdentity, hurtSounds, voicePitch, showInArmor),
                PhysicsOptions(breastPhysics, armorPhysics, buoyancy, floppiness),
                BreastOptions(bustSize, xOffset, yOffset, zOffset, uniBoob, cleavage)
            )
        )
    }

    override fun write(user: ModUser, output: CraftOutputStream) {
        val (general, physics, breast) = user.configuration
        output.writeUUID(user.userId)
        output.writeEnum(general.genderIdentity)
        output.writeFloat(breast.bustSize)
        output.writeBoolean(general.hurtSounds)
        if (hasVoicePitch) output.writeFloat(general.voicePitch)
        output.writeBoolean(physics.breastPhysics)
        if (hasArmorPhysics) output.writeBoolean(physics.armorPhysics)
        output.writeBoolean(general.showInArmor)
        output.writeFloat(physics.buoyancy)
        output.writeFloat(physics.floppiness)
        output.writeFloat(breast.xOffset)
        output.writeFloat(breast.yOffset)
        output.writeFloat(breast.zOffset)
        output.writeBoolean(breast.uniBoob)
        output.writeFloat(breast.cleavage)
    }
}
