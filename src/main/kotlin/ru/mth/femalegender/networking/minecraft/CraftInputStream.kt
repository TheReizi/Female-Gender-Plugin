package ru.mth.femalegender.networking.minecraft

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

class CraftInputStream(input: InputStream) : DataInputStream(input) {

    companion object {
        fun ofBytes(bytes: ByteArray): CraftInputStream = CraftInputStream(ByteArrayInputStream(bytes))
    }

    fun readVarInt(): Int {
        var value = 0
        var position = 0
        while (true) {
            val currentByte = readByte()
            value = value or ((currentByte.toInt() and CraftDataConstants.SEGMENT_BITS) shl position)
            if (currentByte.toInt() and CraftDataConstants.CONTINUE_BIT == 0) return value
            position += 7
            if (position >= 32) throw IOException("VarInt is too big")
        }
    }

    fun readVarLong(): Long {
        var value = 0L
        var position = 0
        while (true) {
            val currentByte = readByte()
            value = value or ((currentByte.toLong() and CraftDataConstants.SEGMENT_BITS.toLong()) shl position)
            if (currentByte.toInt() and CraftDataConstants.CONTINUE_BIT == 0) return value
            position += 7
            if (position >= 64) throw IOException("VarLong is too big")
        }
    }

    fun readUUID(): UUID = UUID(readLong(), readLong())

    inline fun <reified T : Enum<T>> readEnum(): T = enumValues<T>()[readVarInt()]

    fun <K, V> readMap(maxSize: Int, keyReader: () -> K, valueReader: () -> V): Map<K, V> {
        val size = readVarInt()
        if (size > maxSize) throw IOException("Map is too large ($size > $maxSize)")
        val map = LinkedHashMap<K, V>(size)
        repeat(size) { map[keyReader()] = valueReader() }
        return map
    }
}
