package astrocard.telegrambot.telegrambotmessagesender;

import astrocard.CelestialObject;
import astrocard.entity.Descriptions;
import astrocard.telegrambot.eventstate.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TelegramBotMessageSender {
    public static void sendDefaultMessage(long chatId, String text, TelegramClient telegramClient){
        InlineKeyboardButton natalButton = new InlineKeyboardButton("Расчет натальной карты");
        natalButton.setCallbackData("NATAL_BUTTON");

        InlineKeyboardButton predictButton = new InlineKeyboardButton("Прогноз");
        predictButton.setCallbackData("PREDICT_BUTTON");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Отмена");
        cancelButton.setCallbackData("CANCEL_BUTTON");

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(natalButton);
        row.add(predictButton);
        row.add(cancelButton);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    public static void sendPlanetsSignsHousesMenuMessage(long chatId, String text, TelegramClient telegramClient, List<Descriptions> descriptions){
        InlineKeyboardButton planets_in_signs_Button = new InlineKeyboardButton("Планеты в знаках");
        planets_in_signs_Button.setCallbackData("PLANETS_IN_SIGNS_BUTTON");

        InlineKeyboardButton planets_in_houses_Button = new InlineKeyboardButton("Планеты в домах");
        planets_in_houses_Button.setCallbackData("PLANETS_IN_HOUSES_BUTTON");

        /*InlineKeyboardButton houses_in_signs_Button = new InlineKeyboardButton("Дома в знаках");
        houses_in_signs_Button.setCallbackData("HOUSES_IN_SIGNS_BUTTON");*/

        InlineKeyboardButton button = new InlineKeyboardButton("Прогноз");
        button.setCallbackData("PREDICT_BUTTON");

        InlineKeyboardRow row1 = new InlineKeyboardRow();
        InlineKeyboardRow row2 = new InlineKeyboardRow();
        InlineKeyboardRow row3 = new InlineKeyboardRow();
        InlineKeyboardRow row4 = new InlineKeyboardRow();
        row1.add(planets_in_signs_Button);
        row2.add(planets_in_houses_Button);
        //row3.add(houses_in_signs_Button);
        row4.add(button);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row1, row2, row3, row4));


        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    public static void sendPlanetsInSignsMessage(long chatId, String text, TelegramClient telegramClient){
        InlineKeyboardRow row = new InlineKeyboardRow();
        List<InlineKeyboardRow> rowList = new ArrayList<>();
        int i = 0;
        for (CelestialObject celestialObject : CelestialObject.values()) {
            InlineKeyboardButton button = new InlineKeyboardButton(celestialObject.getName());
            button.setCallbackData(celestialObject.name());
            row.add(button);
            i++;

            if (i % 3 == 0) {
                rowList.add(row);
                row = new InlineKeyboardRow(); // новая строка
            }
        }
        // Добавим последнюю строку, если остались кнопки
        if (!row.isEmpty()) {
            rowList.add(row);
        }
        InlineKeyboardRow rowNatal = new InlineKeyboardRow();
        InlineKeyboardButton button = new InlineKeyboardButton("Моя карта");
        button.setCallbackData("BACK_TO_NATAL_FROM_DESCRIPTION");
        rowNatal.add(button);
        rowList.add(rowNatal);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rowList);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public static void sendOnePlanetInSign(long chatId, BotState botState, String planet, List<Descriptions> descriptions, TelegramClient telegramClient){
        String text;
        if(botState==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU){
            text = descriptions.stream().filter((d)-> Objects.equals(d.getObject(), planet) && d.getPosition().length()>2).findFirst().get().getDescription();
        }
        else {
            text = descriptions.stream().filter((d)-> Objects.equals(d.getObject(), planet) && d.getPosition().length()<=2).findFirst().get().getDescription();

        }
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        InlineKeyboardButton button1 = new InlineKeyboardButton("Моя карта");
        button1.setCallbackData("BACK_TO_NATAL_FROM_DESCRIPTION");
        InlineKeyboardRow row2 = new InlineKeyboardRow();
        InlineKeyboardButton button2 = new InlineKeyboardButton("Назад к планетам");
        button2.setCallbackData("BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET");
        row1.add(button1);
        row2.add(button2);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row1, row2));
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public static void sendYesNoMessage(long chatId, String text, TelegramClient telegramClient){
        InlineKeyboardButton yesButton = new InlineKeyboardButton("Да");
        yesButton.setCallbackData("YES_BUTTON");

        InlineKeyboardButton noButton = new InlineKeyboardButton("Нет");
        noButton.setCallbackData("NO_BUTTON");



        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(yesButton);
        row.add(noButton);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendForecastMessage(long chatId, String text, TelegramClient telegramClient){
        InlineKeyboardButton natalButton = new InlineKeyboardButton("К натальной карте");
        natalButton.setCallbackData("BACK_TO_NATAL_FROM_DESCRIPTION");


        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(natalButton);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("HTML")
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
