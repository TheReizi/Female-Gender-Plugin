package ru.mth.femalegender.wildfire.setup

data class PhysicsOptions(
    val breastPhysics: Boolean,
    val armorPhysics: Boolean = false,
    val buoyancy: Float,
    val floppiness: Float
)
