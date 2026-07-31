package ru.mth.femalegender.wildfire.setup.uv

data class UVLayout(val quads: Map<UVDirection, UVQuad> = emptyMap()) {
    companion object {
        val EMPTY = UVLayout()
    }
}
