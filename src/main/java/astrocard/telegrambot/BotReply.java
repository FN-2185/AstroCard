package astrocard.telegrambot;

import astrocard.CelestialObject;
import astrocard.chart.ChartRequest;

public record BotReply(View view, String text, ChartRequest chart, CelestialObject planet) {
    public enum View { MENU, INPUT, CHART, FORECAST, PLANETS_SIGN, PLANETS_HOUSE,
        DESCRIPTION_SIGN, DESCRIPTION_HOUSE, NOTICE, NONE }
    public static BotReply of(View view, String text) { return new BotReply(view, text, null, null); }
    public static BotReply chart(View view, ChartRequest request) { return new BotReply(view, "", request, null); }
}
