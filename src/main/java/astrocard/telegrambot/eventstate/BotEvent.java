package astrocard.telegrambot.eventstate;

public enum BotEvent {
    START_COMMAND,        // Пользователь ввёл /start
    SELECT_NATAL_CHART,   // Нажал "Натальная карта"
    SELECT_FORECAST,      // Нажал "Прогноз"
    USER_HAS_CARD,        // У пользователя есть карта
    USER_HAS_NO_CARD,     // У пользователя нет карты

    ENTERED_COUNTRY,      // Введена страна
    ENTERED_CITY,         // Введён город
    ENTERED_DATE,         // Введена дата
    ENTERED_TIME,
    NATAL_CHART_GET,

    BACK_TO_START,         // Возврат к приветствию из любого состояния
    SELECT_DESCRIPTION_PLANET_IN_SIGN_MENU,
    SELECT_DESCRIPTION_PLANET_IN_HOUSE_MENU,
    SELECT_PLANET,
    BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET,
    BACK_TO_NATAL_RESULT
}
