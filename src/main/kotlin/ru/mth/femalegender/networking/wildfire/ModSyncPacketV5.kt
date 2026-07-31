package ru.mth.femalegender.networking.wildfire

import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser
import ru.mth.femalegender.wildfire.setup.uv.UVDirection
import ru.mth.femalegender.wildfire.setup.uv.UVLayout
import ru.mth.femalegender.wildfire.setup.uv.UVLayouts
import ru.mth.femalegender.wildfire.setup.uv.UVQuad

/**
 * Формат v5 (Fabric/NeoForge Female-Gender-Mod 5.0.0+) переписан на кодеки
 * Minecraft, но порядок и типы общих полей совпадают с V4 — меняется только
 * то, что armorPhysics больше не существует, а в конце добавилась секция
 * UV-layout (кастомная раскладка текстуры груди). Поэтому общая часть
 * переиспользуется через делегирование к AbstractModSyncPacket, а UV-секция
 * дочитывается/дописывается поверх неё.
 */
object ModSyncPacketV5 : ModSyncPacket {
    override val version = 5
    override val modRange = "5.0.0 - ?.?.?"

    // version/modRange здесь не используются — регистр форматов обращается к ним через ModSyncPacketV5 напрямую.
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

    private fun CraftInputStream.readUVLayout(): UVLayout =
        UVLayout(readMap(MAX_UV_QUADS, keyReader = { readEnum<UVDirection>() }, valueReader = { readUVQuad() }))

    private fun CraftOutputStream.writeUVLayout(layout: UVLayout) {
        writeMap(layout.quads, keyWriter = { writeEnum(it) }, valueWriter = { writeUVQuad(it) })
    }

    private fun CraftInputStream.readUVLayouts(): UVLayouts {
        val skin = UVLayouts.Layer(readUVLayout(), readUVLayout())
        val overlay = UVLayouts.Layer(readUVLayout(), readUVLayout())
        return UVLayouts(skin, overlay)
    }

    private fun CraftOutputStream.writeUVLayouts(layouts: UVLayouts) {
        writeUVLayout(layouts.skin.left)
        writeUVLayout(layouts.skin.right)
        writeUVLayout(layouts.overlay.left)
        writeUVLayout(layouts.overlay.right)
    }
}
