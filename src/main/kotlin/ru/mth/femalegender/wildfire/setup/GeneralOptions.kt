package ru.mth.femalegender.wildfire.setup

data class GeneralOptions(
    val genderIdentity: GenderIdentities,
    val hurtSounds: Boolean,
    val voicePitch: Float = 0f,
    val showInArmor: Boolean = false
)
