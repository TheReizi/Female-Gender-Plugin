package ru.mth.femalegender.wildfire

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class UserManager {
    val users: MutableMap<UUID, ModUser> = ConcurrentHashMap()
}
