package ru.mth.femalegender.wildfire

import ru.mth.femalegender.wildfire.setup.ModConfiguration
import java.util.UUID

data class ModUser(val userId: UUID, val configuration: ModConfiguration)
