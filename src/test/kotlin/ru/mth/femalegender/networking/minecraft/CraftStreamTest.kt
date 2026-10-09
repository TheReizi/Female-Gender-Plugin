package ru.mth.femalegender.networking.minecraft

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import java.io.ByteArrayOutputStream
import java.io.IOException

class CraftStreamTest {

    @Test
    fun `VarInt round trips boundary values`() {
        val values = listOf(0, 1, 127, 128, 255, 16_383, 16_384, Int.MAX_VALUE, -1, Int.MIN_VALUE)

        val encoded = ByteArrayOutputStream().use { bytes ->
            CraftOutputStream(bytes).use { output -> values.forEach(output::writeVarInt) }
            bytes.toByteArray()
        }

        CraftInputStream.ofBytes(encoded).use { input ->
            values.forEach { assertEquals(it, input.readVarInt()) }
            assertEquals(0, input.available())
        }
    }

    @Test
    fun `VarLong round trips boundary values`() {
        val values = listOf(0L, 1L, 127L, 128L, 16_383L, Long.MAX_VALUE, -1L, Long.MIN_VALUE)

        val encoded = ByteArrayOutputStream().use { bytes ->
            CraftOutputStream(bytes).use { output -> values.forEach(output::writeVarLong) }
            bytes.toByteArray()
        }

        CraftInputStream.ofBytes(encoded).use { input ->
            values.forEach { assertEquals(it, input.readVarLong()) }
            assertEquals(0, input.available())
        }
    }

    @Test
    fun `rejects malformed enum and map payloads`() {
        val unknownEnum = byteArrayOf(99)
        CraftInputStream.ofBytes(unknownEnum).use { input ->
            assertFailsWith<IOException> { input.readEnum<TestEnum>() }
        }

        val negativeSize = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F)
        CraftInputStream.ofBytes(negativeSize).use { input ->
            assertFailsWith<IOException> { input.readMap(5, { 0 }, { 0 }) }
        }
    }

    @Test
    fun `rejects VarInt longer than five bytes`() {
        val tooLong = byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0)
        CraftInputStream.ofBytes(tooLong).use { input ->
            assertFailsWith<IOException> { input.readVarInt() }
        }
    }

    private enum class TestEnum { FIRST, SECOND }
}
