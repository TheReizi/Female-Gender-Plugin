package ru.mth.femalegender.networking.wildfire

import kotlin.test.Test
import kotlin.test.assertEquals
import ru.mth.femalegender.networking.minecraft.CraftInputStream
import ru.mth.femalegender.networking.minecraft.CraftOutputStream
import ru.mth.femalegender.wildfire.ModUser
import ru.mth.femalegender.wildfire.setup.BreastOptions
import ru.mth.femalegender.wildfire.setup.GenderIdentities
import ru.mth.femalegender.wildfire.setup.GeneralOptions
import ru.mth.femalegender.wildfire.setup.ModConfiguration
import ru.mth.femalegender.wildfire.setup.PhysicsOptions
import ru.mth.femalegender.wildfire.setup.uv.UVDirection
import ru.mth.femalegender.wildfire.setup.uv.UVLayout
import ru.mth.femalegender.wildfire.setup.uv.UVLayouts
import ru.mth.femalegender.wildfire.setup.uv.UVQuad
import java.io.ByteArrayOutputStream
import java.util.UUID

class ModSyncPacketTest {

    @Test
    fun `V2 V3 and V4 retain fields available in their wire format`() {
        roundTrip(ModSyncPacketV2, sampleUser()).also { restored ->
            assertEquals(0f, restored.configuration.generalOptions.voicePitch)
            assertEquals(sampleUser().configuration.physicsOptions.armorPhysics, restored.configuration.physicsOptions.armorPhysics)
        }
        roundTrip(ModSyncPacketV3, sampleUser()).also { restored ->
            assertEquals(0f, restored.configuration.generalOptions.voicePitch)
            assertEquals(false, restored.configuration.physicsOptions.armorPhysics)
        }
        roundTrip(ModSyncPacketV4, sampleUser()).also { restored ->
            assertEquals(sampleUser().configuration.generalOptions.voicePitch, restored.configuration.generalOptions.voicePitch)
            assertEquals(false, restored.configuration.physicsOptions.armorPhysics)
        }
    }

    @Test
    fun `V5 round trip fills partial UV layouts with client safe defaults`() {
        val user = sampleUser().copy(
            configuration = sampleUser().configuration.copy(
                breastOptions = sampleUser().configuration.breastOptions.copy(
                    uvLayouts = UVLayouts(
                        skin = UVLayouts.Layer(UVLayout(mapOf(UVDirection.NORTH to UVQuad(1, 2, 3, 4))), UVLayout()),
                        overlay = UVLayouts.Layer(UVLayout(), UVLayout())
                    )
                )
            )
        )

        val restored = roundTrip(ModSyncPacketV5, user)
        val layouts = restored.configuration.breastOptions.uvLayouts
        val expectedLeftSkin = UVLayouts.DEFAULT.skin.left.quads.toMutableMap().apply {
            this[UVDirection.NORTH] = UVQuad(1, 2, 3, 4)
        }
        assertEquals(UVQuad(1, 2, 3, 4), layouts.skin.left.quads[UVDirection.NORTH])
        assertEquals(expectedLeftSkin, layouts.skin.left.quads)
        assertEquals(UVLayouts.DEFAULT.skin.right.quads, layouts.skin.right.quads)
        assertEquals(UVLayouts.DEFAULT.overlay.left.quads, layouts.overlay.left.quads)
        assertEquals(UVLayouts.DEFAULT.overlay.right.quads, layouts.overlay.right.quads)
    }

    private fun roundTrip(packet: ModSyncPacket, user: ModUser): ModUser {
        val bytes = ByteArrayOutputStream().use { payload ->
            CraftOutputStream(payload).use { packet.write(user, it) }
            payload.toByteArray()
        }
        return CraftInputStream.ofBytes(bytes).use { packet.read(it) }
    }

    private fun sampleUser() = ModUser(
        UUID.fromString("42f965e3-c17f-4ee3-8a8e-e099a59754b7"),
        ModConfiguration(
            GeneralOptions(GenderIdentities.OTHER, hurtSounds = true, voicePitch = 0.73f, showInArmor = true),
            PhysicsOptions(breastPhysics = true, armorPhysics = true, buoyancy = 0.42f, floppiness = 0.91f),
            BreastOptions(bustSize = 1.4f, xOffset = 0.1f, yOffset = -0.2f, zOffset = 0.3f, uniBoob = true, cleavage = 0.6f)
        )
    )
}
