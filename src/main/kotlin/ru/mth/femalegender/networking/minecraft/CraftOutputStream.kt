package ru.mth.femalegender.networking.minecraft

import java.io.DataOutputStream
import java.io.OutputStream
import java.util.UUID

class CraftOutputStream(output: OutputStream) : DataOutputStream(output) {

    fun writeVarInt(value: Int) {
        var remaining = value
        while (remaining and -128 != 0) {
            writeByte((remaining and CraftDataConstants.SEGMENT_BITS) or CraftDataConstants.CONTINUE_BIT)
            remaining = remaining ushr 7
        }
        writeByte(remaining)
    }

    fun writeVarLong(value: Long) {
        var remaining = value
        while (remaining and -128L != 0L) {
            writeByte(((remaining and 127L) or 128L).toInt())
            remaining = remaining ushr 7
        }
        writeByte(remaining.toInt())
    }

    fun writeUUID(uuid: UUID) {
        writeLong(uuid.mostSignificantBits)
        writeLong(uuid.leastSignificantBits)
    }

    fun <T : Enum<T>> writeEnum(value: T) = writeVarInt(value.ordinal)

    fun <K, V> writeMap(map: Map<K, V>, keyWriter: (K) -> Unit, valueWriter: (V) -> Unit) {
        writeVarInt(map.size)
        map.forEach { (key, value) ->
            keyWriter(key)
            valueWriter(value)
        }
    }
}
