package astrocard.telegrambot;

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

    BACK_TO_START         // Возврат к приветствию из любого состояния
}
