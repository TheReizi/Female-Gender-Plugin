package ru.mth.femalegender.networking.wildfire

import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser
import ru.mth.femalegender.wildfire.setup.uv.UVDirection
import ru.mth.femalegender.wildfire.setup.uv.UVLayout
import ru.mth.femalegender.wildfire.setup.uv.UVLayouts
import ru.mth.femalegender.wildfire.setup.uv.UVQuad

object ModSyncPacketV5 : ModSyncPacket {
    override val version = 5
    override val modRange = "5.0.0 - ?.?.?"

    private val common = object : AbstractModSyncPacket(hasArmorPhysics = false, hasVoicePitch = true) {
        override val version = 5
        override val modRange = ""
    }

    private const val MAX_UV_QUADS = 5 // UVDirection.entries.size

    override fun read(input: CraftInputStream): ModUser {
        val user = common.read(input)
        val uvLayouts = input.readUVLayouts()
        return user.copy(
            configuration = user.configuration.copy(
                breastOptions = user.configuration.breastOptions.copy(uvLayouts = uvLayouts)
            )
        )
    }

    override fun write(user: ModUser, output: CraftOutputStream) {
        common.write(user, output)
        output.writeUVLayouts(user.configuration.breastOptions.uvLayouts)
    }

    private fun CraftInputStream.readUVQuad(): UVQuad = UVQuad(readVarInt(), readVarInt(), readVarInt(), readVarInt())

    private fun CraftOutputStream.writeUVQuad(quad: UVQuad) {
        writeVarInt(quad.x1)
        writeVarInt(quad.y1)
        writeVarInt(quad.x2)
        writeVarInt(quad.y2)
    }

    // fallback дополняет любые отсутствующие направления дефолтными квадами мода — карта на
    // проводе никогда не должна быть неполной, иначе клиент 5.0.0+ падает с NPE (см. UVLayouts.DEFAULT).
    private fun CraftInputStream.readUVLayout(fallback: UVLayout): UVLayout {
        val quads = readMap(MAX_UV_QUADS, keyReader = { readEnum<UVDirection>() }, valueReader = { readUVQuad() })
        return UVLayout(fallback.quads + quads)
    }

    private fun CraftOutputStream.writeUVLayout(layout: UVLayout, fallback: UVLayout) {
        writeMap(fallback.quads + layout.quads, keyWriter = { writeEnum(it) }, valueWriter = { writeUVQuad(it) })
    }

    private fun CraftInputStream.readUVLayouts(): UVLayouts {
        val default = UVLayouts.DEFAULT
        val skin = UVLayouts.Layer(readUVLayout(default.skin.left), readUVLayout(default.skin.right))
        val overlay = UVLayouts.Layer(readUVLayout(default.overlay.left), readUVLayout(default.overlay.right))
        return UVLayouts(skin, overlay)
    }

    private fun CraftOutputStream.writeUVLayouts(layouts: UVLayouts) {
        val default = UVLayouts.DEFAULT
        writeUVLayout(layouts.skin.left, default.skin.left)
        writeUVLayout(layouts.skin.right, default.skin.right)
        writeUVLayout(layouts.overlay.left, default.overlay.left)
        writeUVLayout(layouts.overlay.right, default.overlay.right)
    }
}
