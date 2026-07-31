package ru.mth.femalegender.wildfire.setup

import ru.mth.femalegender.wildfire.setup.uv.UVLayouts

data class BreastOptions(
    val bustSize: Float,
    val xOffset: Float,
    val yOffset: Float,
    val zOffset: Float,
    val uniBoob: Boolean,
    val cleavage: Float,
    val uvLayouts: UVLayouts = UVLayouts.EMPTY
)
