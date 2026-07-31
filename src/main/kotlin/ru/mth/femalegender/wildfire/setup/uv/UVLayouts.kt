package ru.mth.femalegender.wildfire.setup.uv

data class UVLayouts(val skin: Layer, val overlay: Layer) {

    data class Layer(val left: UVLayout, val right: UVLayout) {
        companion object {
            val EMPTY = Layer(UVLayout.EMPTY, UVLayout.EMPTY)
        }
    }

    companion object {
        val EMPTY = UVLayouts(Layer.EMPTY, Layer.EMPTY)
    }
}
