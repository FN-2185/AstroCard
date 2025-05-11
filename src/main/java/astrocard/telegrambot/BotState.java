package astrocard.telegrambot;

public enum BotState {
    START,                // Стартовое приветствие
    CHOOSE_ACTION,        // Выбор действия: натальная карта или прогноз

    NATAL_COUNTRY,        // Ввод страны
    NATAL_CITY,           // Ввод города
    NATAL_DATE,           // Ввод даты
    NATAL_TIME,
    //NATAL_RESULT,         // Вывод натальной карты

    FORECAST_CHECK_CARD,  // Проверка наличия карты
    FORECAST_UNAVAILABLE, // Сообщение, что функция в разработке
}
