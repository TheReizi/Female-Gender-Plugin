package ru.mth.femalegender.core.config

enum class MessageKey(val key: String, val defaultMessage: String) {
    // General
    ONLY_PLAYERS("only_players", "<red>Эта команда только для игроков!</red>"),
    NO_PERMISSION("no_permission", "<red>У вас нет прав для использования этой команды!</red>"),
    USAGE("usage", "<red>Справка: {usage}</red>"),
    PLAYER_NOT_FOUND("player_not_found", "<red>Игрок <yellow>{player}</yellow> не найден!</red>"),

    // Commands
    CMD_GIVE_USAGE("cmd_give_usage", "<red>/weapon give <игрок> <тип></red>"),
    CMD_AMMO_USAGE("cmd_ammo_usage", "<red>/weapon ammo <игрок> <тип> [количество]</red>"),
    CMD_WEAPON_GIVEN("cmd_weapon_given", "<green>Выдано {name} <green>→</green> <white>{player}</white></green>"),
    CMD_WEAPON_RECEIVED("cmd_weapon_received", "<green>Вы получили {name}</green>"),
    CMD_UNKNOWN_TYPE("cmd_unknown_type", "<red>Неизвестный тип: <white>{type}</white>. <dark_gray>/weapon list</dark_gray></red>"),
    CMD_AMMO_GIVEN("cmd_ammo_given", "<green>Выдано <white>{amount}</white> патронов {name} <green>→</green> <white>{player}</white></green>"),
    CMD_WEAPON_LIST_HEADER("cmd_weapon_list_header", "<yellow>Виды оружия:</yellow>"),
    CMD_WEAPON_LIST_CATEGORY("cmd_weapon_list_category", "<gold>{category}:</gold>"),
    CMD_WEAPON_LIST_ITEM("cmd_weapon_list_item", "  <white>{key}</white>"),
    CMD_HELP_GIVE("cmd_help_give", "<yellow>/weapon give <игрок> <тип></yellow>"),
    CMD_HELP_AMMO("cmd_help_ammo", "<yellow>/weapon ammo <игрок> <тип> [количество]</yellow>"),
    CMD_HELP_LIST("cmd_help_list", "<yellow>/weapon list</yellow>"),

    // Action bars — shooting
    ACTIONBAR_RELOADING("actionbar_reloading", "<red>Перезарядка...</red>"),
    ACTIONBAR_MAGAZINE_EMPTY("actionbar_magazine_empty", "<red>Магазин пуст! <dark_gray>F</dark_gray> <gray>— перезарядка</gray></red>"),
    ACTIONBAR_AMMO_STATUS("actionbar_ammo_status", "{name} <dark_gray>|</dark_gray> <white>{current}</white> <dark_gray>/</dark_gray> <white>{max}</white>"),

    // Action bars — scope
    ACTIONBAR_SCOPE_OFF("actionbar_scope_off", "<gray>Прицел снят</gray>"),
    ACTIONBAR_SCOPE_ON("actionbar_scope_on", "<green>Прицел</green> <dark_gray>|</dark_gray> <gray>ПКМ — снять</gray>"),

    // Action bars — reload
    ACTIONBAR_ALREADY_RELOADING("actionbar_already_reloading", "<red>Уже перезаряжается...</red>"),
    ACTIONBAR_MAGAZINE_FULL("actionbar_magazine_full", "<green>Магазин полон</green>"),
    ACTIONBAR_NO_AMMO("actionbar_no_ammo", "<red>Нет патронов для {name}</red>"),
    ACTIONBAR_RELOAD_INTERRUPTED("actionbar_reload_interrupted", "<red>Перезарядка прервана</red>"),
    ACTIONBAR_RELOAD_PROGRESS("actionbar_reload_progress", "<yellow>Перезарядка</yellow> {bar} <dark_gray>(<white>{count}</white> патр.)</dark_gray>"),
    ACTIONBAR_RELOAD_DONE("actionbar_reload_done", "<green>Готово!</green> <white>{current}</white> <dark_gray>/</dark_gray> <white>{max}</white> <dark_gray>(<white>{consumed}</white> патр.)</dark_gray>"),

    // GPS Navigation
    GPS_TARGET_SET("gps_target_set", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> Цель: <white>{x} {y} {z}</white> <dark_gray>({distance}м)</dark_gray>"),
    GPS_PATH_NOT_FOUND("gps_path_not_found", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <red>Не удалось найти путь.</red>"),
    GPS_TARGET_REACHED("gps_target_reached", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <green>Цель достигнута!</green>"),
    GPS_TARGET_CLEARED("gps_target_cleared", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <gray>Навигация отключена.</gray>"),
    GPS_NO_ACTIVE("gps_no_active", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <gray>Нет активной навигации.</gray>"),
    GPS_DIFF_WORLD("gps_diff_world", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <red>Цель находится в другом мире!</red>"),
    GPS_ACTION_BAR("gps_action_bar", "<aqua>⊙ {distance}м</aqua>"),
    GPS_INFO_ACTIVE("gps_info_active", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> Цель: <white>{x} {y} {z}</white> <dark_gray>·</dark_gray> <aqua>{distance}м</aqua>"),
    GPS_CATEGORY_NOT_FOUND("gps_category_not_found", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <red>Категория <yellow>{category}</yellow> не найдена.</red>"),
    GPS_CATEGORY_EMPTY("gps_category_empty", "<aqua>GPS</aqua> <dark_gray>|</dark_gray> <red>В категории <yellow>{category}</yellow> нет точек.</red>"),

    // Knockout system
    KO_CHAT_MESSAGE("ko_chat_message", "<gray>Состояние: <white>Ожидание помощи</white> <dark_gray>|</dark_gray> <red><bold><click:run_command:'/knockout die'>[УМЕРЕТЬ]</click></bold></red>"),
    KO_NO_HOSPITAL("ko_no_hospital", "<red>Точка больницы не установлена! Обратитесь к администратору.</red>"),
    KO_COUNTDOWN("ko_countdown", "<red>⚠ Авто-смерть через <white>{seconds}</white> сек.</red>"),

    // Phone (телефон — вызов экстренных служб)
    PHONE_CALL_PROMPT("phone_call_prompt", "<aqua>📞</aqua> <white>Напишите своё обращение в {service}.</white> <gray>Чтобы отменить отправку — напишите</gray> <yellow>отменить</yellow><gray>.</gray>"),
    PHONE_CALL_CANCELLED("phone_call_cancelled", "<gray>📞 Обращение отменено.</gray>"),
    PHONE_CALL_SENT("phone_call_sent", "<green>📞 Ваше обращение отправлено. Ожидайте ответа.</green>"),
    PHONE_DIAL_CLEARED("phone_dial_cleared", "<gray>📞 Номер сброшен.</gray>"),
    PHONE_DIAL_UNKNOWN("phone_dial_unknown", "<red>📞 Неизвестный номер: <white>{number}</white>. Проверьте набранное.</red>"),
    PHONE_DIAL_EMPTY("phone_dial_empty", "<gray>📞 Наберите номер перед звонком.</gray>"),
    PHONE_DIAL_MAX("phone_dial_max", "<red>📞 Максимум <white>{max}</white> цифр.</red>"),

    // Custom Items
    CITEM_NOT_FOUND("citem_not_found", "<red>Предмет <white>{id}</white> не найден. Проверьте ID.</red>"),
    CITEM_GIVEN("citem_given", "<green>Предмет <white>{name}</white> <green>→</green> <white>{player}</white></green>"),
    CITEM_RECEIVED("citem_received", "<green>Вы получили <white>{name}</white></green>"),
    CITEM_PLAYER_NOT_FOUND("citem_player_not_found", "<red>Игрок <white>{player}</white> не найден.</red>"),

    // Passport (паспорт)
    PASSPORT_USAGE("passport_usage", "<red>Использование: /passport show <ID></red>"),
    PASSPORT_NO_PASSPORT("passport_no_passport", "<red>У вас нет паспорта.</red>"),
    PASSPORT_TARGET_NOT_FOUND("passport_target_not_found", "<red>Игрок с ID <white>{id}</white> не найден.</red>"),
    PASSPORT_TARGET_OFFLINE("passport_target_offline", "<red>Этот игрок сейчас не в сети.</red>"),
    PASSPORT_SHOWN("passport_shown", "<green>Вы показали паспорт игроку <white>{player}</white>.</green>"),
    PASSPORT_RECEIVED("passport_received", "<yellow><white>{player}</white> показывает вам свой паспорт.</yellow>"),
    PASSPORT_TOO_FAR("passport_too_far", "<red>Игрок должен находиться рядом с вами (радиус 4 блока).</red>"),
    PASSPORT_SHOW_FAILED("passport_show_failed", "<red>Не удалось показать паспорт. Проверьте, установлен ли HudEngine и настроен ли popup 'passport'.</red>"),
    PASSPORT_CLOSE_HINT("passport_close_hint", "<#bc5862>Нажмите <white>F</white>, чтобы закрыть паспорт"),

    // AFK system
    AFK_KICK("afk_kick", "<red>Вы были отключены за бездействие.</red>"),
    AFK_WARNING("afk_warning", "<yellow>⚠ АФК! Вы будете отключены через <white>{seconds}</white> сек.</yellow>"),

    // Realty system
    REALTY_CREATING("realty_creating", "<gray>Анализирую комнату...</gray>"),
    REALTY_CREATED("realty_created", "<green>Недвижимость <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> создана <dark_gray>|</dark_gray> Цена: <white>{price}</white> <dark_gray>|</dark_gray> Налог: <white>{tax}₵</white> <dark_gray>|</dark_gray> Блоков: <white>{blocks}</white></green>"),
    REALTY_DELETED("realty_deleted", "<green>Недвижимость <white>{id}</white> удалена.</green>"),
    REALTY_NOT_FOUND("realty_not_found", "<red>Недвижимость <white>{id}</white> не найдена.</red>"),
    REALTY_ALREADY_EXISTS("realty_already_exists", "<red>Недвижимость <white>{id}</white> уже существует.</red>"),
    REALTY_FLOOD_FAILED("realty_flood_failed", "<red>Не удалось определить границы комнаты. Убедитесь, что все двери закрыты и нет отверстий в стенах.</red>"),
    REALTY_BOUGHT("realty_bought", "<green>Вы купили недвижимость <white>{name}</white> за <white>{price}</white> ₵.</green>"),
    REALTY_NOT_ENOUGH_FUNDS("realty_not_enough_funds", "<red>Недостаточно средств. Цена: <white>{price}</white> ₵.</red>"),
    REALTY_SOLD("realty_sold", "<green>Вы продали недвижимость <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> и получили <white>{price}</white> ₵.</green>"),
    REALTY_NOT_OWNER("realty_not_owner", "<red>Вы не являетесь владельцем этой недвижимости.</red>"),
    REALTY_ALREADY_OWNED("realty_already_owned", "<red>Эта недвижимость уже куплена.</red>"),
    REALTY_NOT_FOR_SALE("realty_not_for_sale", "<red>Эта недвижимость не продаётся.</red>"),
    REALTY_NO_PERMISSION("realty_no_permission", "<red>Вы не можете изменять блоки в чужой зоне.</red>"),
    REALTY_INFO("realty_info", "<gold>▶ <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> <dark_gray>|</dark_gray> Мир: <white>{world}</white> <dark_gray>|</dark_gray> Блоков: <white>{blocks}</white> <dark_gray>|</dark_gray> Владелец: <white>{owner}</white> <dark_gray>|</dark_gray> Цена: <white>{price}</white> <dark_gray>|</dark_gray> Налог: <white>{tax}₵</white> <dark_gray>|</dark_gray> Статус: <white>{status}</white></gold>"),
    REALTY_CHECK_NOT_IN_ZONE("realty_check_not_in_zone", "<gray>Вы не стоите ни в одной зоне недвижимости.</gray>"),

    // Garage system
    GARAGE_CREATING("garage_creating", "<gray>Анализирую гараж...</gray>"),
    GARAGE_CREATED("garage_created", "<green>Гараж <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> создан <dark_gray>|</dark_gray> Цена: <white>{price}</white> <dark_gray>|</dark_gray> Налог: <white>{tax}₵</white> <dark_gray>|</dark_gray> Блоков: <white>{blocks}</white></green>"),
    GARAGE_DELETED("garage_deleted", "<green>Гараж <white>{id}</white> удалён.</green>"),
    GARAGE_NOT_FOUND("garage_not_found", "<red>Гараж <white>{id}</white> не найден.</red>"),
    GARAGE_ALREADY_EXISTS("garage_already_exists", "<red>Гараж <white>{id}</white> уже существует.</red>"),
    GARAGE_FLOOD_FAILED("garage_flood_failed", "<red>Не удалось определить границы гаража. Убедитесь, что все ворота/двери закрыты и нет отверстий в стенах.</red>"),
    GARAGE_BOUGHT("garage_bought", "<green>Вы купили гараж <white>{name}</white> за <white>{price}</white> ₵.</green>"),
    GARAGE_NOT_ENOUGH_FUNDS("garage_not_enough_funds", "<red>Недостаточно средств. Цена: <white>{price}</white> ₵.</red>"),
    GARAGE_SOLD("garage_sold", "<green>Вы продали гараж <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> и получили <white>{price}</white> ₵.</green>"),
    GARAGE_NOT_OWNER("garage_not_owner", "<red>Вы не являетесь владельцем этого гаража.</red>"),
    GARAGE_ALREADY_OWNED("garage_already_owned", "<red>Этот гараж уже куплен.</red>"),
    GARAGE_NOT_FOR_SALE("garage_not_for_sale", "<red>Этот гараж не продаётся.</red>"),
    GARAGE_NO_PERMISSION("garage_no_permission", "<red>Вы не можете изменять блоки в чужом гараже.</red>"),
    GARAGE_INFO("garage_info", "<gold>▶ <white>{name}</white> <dark_gray>(ID: {id})</dark_gray> <dark_gray>|</dark_gray> Мир: <white>{world}</white> <dark_gray>|</dark_gray> Блоков: <white>{blocks}</white> <dark_gray>|</dark_gray> Владелец: <white>{owner}</white> <dark_gray>|</dark_gray> Цена: <white>{price}</white> <dark_gray>|</dark_gray> Налог: <white>{tax}₵</white> <dark_gray>|</dark_gray> Статус: <white>{status}</white></gold>"),
    GARAGE_CHECK_NOT_IN_ZONE("garage_check_not_in_zone", "<gray>Вы не стоите ни в одной зоне гаража.</gray>"),

    // Garage doors (ворота)
    GARAGE_DOOR_OPENED("garage_door_opened", "<green>Ворота открыты.</green>"),
    GARAGE_DOOR_CLOSED("garage_door_closed", "<gray>Ворота закрыты.</gray>"),
    GARAGE_DOOR_NOT_OWNER("garage_door_not_owner", "<red>Это не ваш гараж.</red>"),
}
