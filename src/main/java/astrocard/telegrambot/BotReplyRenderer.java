package astrocard.telegrambot;

import astrocard.*;
import astrocard.service.DescriptionSenderService;
import astrocard.telegrambot.natalchartsender.ChartSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*;
import java.util.*;

@Component
public class BotReplyRenderer {
    public record Message(String text, boolean html, List<InlineKeyboardRow> keyboard) {}
    private final NatalChartService charts;
    private final ChartSender renderer;
    private final DescriptionSenderService descriptions;

    public BotReplyRenderer(NatalChartService charts, ChartSender renderer, DescriptionSenderService descriptions) {
        this.charts = charts; this.renderer = renderer; this.descriptions = descriptions;
    }

    public Message render(BotReply reply) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        String text = reply.text();
        boolean html = false;
        switch (reply.view()) {
            case MENU -> {
                keyboard.add(row("Создать натальную карту", "NATAL_BUTTON"));
                if (reply.chart() != null) keyboard.add(row("Моя карта", "SAVED_CHART"));
            }
            case INPUT -> keyboard.add(row("Отменить ввод", "CANCEL_BUTTON"));
            case CHART -> {
                text = renderer.render(charts.getChart(reply.chart()), "Натальная карта"); html = true;
                keyboard.add(row("Планеты в знаках", "PLANETS_IN_SIGNS_BUTTON"));
                keyboard.add(row("Планеты в домах", "PLANETS_IN_HOUSES_BUTTON"));
                keyboard.add(row("Прогноз", "PREDICT_BUTTON"));
                keyboard.add(row("Ввести данные заново", "NATAL_BUTTON"));
            }
            case FORECAST -> {
                text = renderer.render(charts.getChart(reply.chart()), "Положения планет на "
                        + reply.chart().instant().toString()); html = true;
                keyboard.add(row("Моя карта", "BACK_TO_NATAL_FROM_DESCRIPTION"));
            }
            case PLANETS_SIGN, PLANETS_HOUSE -> {
                InlineKeyboardRow row = new InlineKeyboardRow();
                for (CelestialObject planet : CelestialObject.values()) {
                    row.add(button(planet.getName(), planet.name()));
                    if (row.size() == 3) { keyboard.add(row); row = new InlineKeyboardRow(); }
                }
                if (!row.isEmpty()) keyboard.add(row);
                keyboard.add(row("Моя карта", "BACK_TO_NATAL_FROM_DESCRIPTION"));
            }
            case DESCRIPTION_SIGN, DESCRIPTION_HOUSE -> {
                text = descriptions.describe(charts.getChart(reply.chart()), reply.planet().getSwissephId(),
                        reply.view() == BotReply.View.DESCRIPTION_HOUSE);
                keyboard.add(row("Назад к планетам", "BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET"));
                keyboard.add(row("Моя карта", "BACK_TO_NATAL_FROM_DESCRIPTION"));
            }
            case NOTICE, NONE -> {}
        }
        return new Message(text, html, List.copyOf(keyboard));
    }

    private InlineKeyboardButton button(String text, String callback) {
        return InlineKeyboardButton.builder().text(text).callbackData(callback).build();
    }
    private InlineKeyboardRow row(String text, String callback) {
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button(text, callback));
        return row;
    }
}
