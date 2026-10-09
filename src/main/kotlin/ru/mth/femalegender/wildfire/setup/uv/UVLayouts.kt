package ru.mth.femalegender.wildfire.setup.uv

data class UVLayouts(val skin: Layer, val overlay: Layer) {

    data class Layer(val left: UVLayout, val right: UVLayout)

    companion object {
        /**
         * Совпадает 1:1 с дефолтными UV-координатами из мода
         * (Configuration.LEFT_BREAST_UV_LAYOUT и соседние константы).
         *
         * Клиент 5.0.0+ падает с NPE в WildfireModelRenderer.initQuads/GenderLayer.renderBox,
         * если для какого-то направления вместо квада приходит null (это то, что получается
         * на приёмнике, если отправить пустую/неполную карту — UVLayout.fillMissing() заполняет
         * пропуски null'ами). Поэтому мы НИКОГДА не передаём пустую карту — вместо этого у
         * игроков без реальной UV-кастомизации (v1-v4 или ещё не присланные данные) используются
         * эти дефолты, дополняющие любые частичные данные до полного набора из 5 направлений.
         */
        val DEFAULT = UVLayouts(
            skin = Layer(
                left = UVLayout(
                    mapOf(
                        UVDirection.EAST to UVQuad(24, 21, 27, 26),
                        UVDirection.WEST to UVQuad(16, 21, 20, 26),
                        UVDirection.DOWN to UVQuad(20, 17, 24, 21),
                        UVDirection.UP to UVQuad(20, 25, 24, 27),
                        UVDirection.NORTH to UVQuad(20, 21, 24, 26)
                    )
                ),
                right = UVLayout(
                    mapOf(
                        UVDirection.EAST to UVQuad(28, 21, 32, 26),
                        UVDirection.WEST to UVQuad(21, 21, 24, 26),
                        UVDirection.DOWN to UVQuad(24, 17, 28, 21),
                        UVDirection.UP to UVQuad(24, 25, 28, 27),
                        UVDirection.NORTH to UVQuad(24, 21, 28, 26)
                    )
                )
            ),
            overlay = Layer(
                left = UVLayout(
                    mapOf(
                        UVDirection.EAST to UVQuad(0, 0, 0, 0),
                        UVDirection.WEST to UVQuad(17, 37, 20, 42),
                        UVDirection.DOWN to UVQuad(20, 34, 24, 37),
                        UVDirection.UP to UVQuad(20, 42, 24, 45),
                        UVDirection.NORTH to UVQuad(20, 37, 24, 42)
                    )
                ),
                right = UVLayout(
                    mapOf(
                        UVDirection.EAST to UVQuad(28, 37, 31, 42),
                        UVDirection.WEST to UVQuad(0, 0, 0, 0),
                        UVDirection.DOWN to UVQuad(24, 34, 28, 37),
                        UVDirection.UP to UVQuad(24, 42, 28, 45),
                        UVDirection.NORTH to UVQuad(24, 37, 28, 42)
                    )
                )
            )
        )
    }
}
