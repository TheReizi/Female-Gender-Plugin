package ru.mth.femalegender.core.config

enum class MessageKey(val key: String, val defaultMessage: String) {
    // General
    ONLY_PLAYERS("only_players", "<red>Эта команда только для игроков!</red>"),
    NO_PERMISSION("no_permission", "<red>У вас нет прав для использования этой команды!</red>"),
    USAGE("usage", "<red>Справка: {usage}</red>"),
    PLAYER_NOT_FOUND("player_not_found", "<red>Игрок <yellow>{player}</yellow> не найден!</red>"),
}
