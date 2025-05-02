package astrocard.telegrambot;

import astrocard.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final BotConfig botConfig;
    private final TelegramClient telegramClient;
    private final NatalChartFSMService fsmService;


    private final Map<Long, InputStep> userSteps = new HashMap<>();
    private final Map<Long, Map<String, String>> userInput = new HashMap<>();

    public TelegramBot(BotConfig botConfig, NatalChartFSMService fsmService) {
        this.botConfig = botConfig;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.fsmService = fsmService;
    }

    @Override
    public String getBotToken() {
        return this.botConfig.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                //fsmService.start(chatId);
                sendMessage(chatId, "Я — астрологический бот, который поможет тебе составить натальную карту , " +
                        "узнать расположение планет в момент твоего рождения и получить их толкование. " +
                        "Чтобы начать, выбери один из пунктов ниже:");
                return;
            }

            String reply = fsmService.handleInput(chatId, text);
            sendMessage(chatId, reply);
            return;
        }
        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callBackData) {
                case "NATAL_BUTTON" -> {/*
                    userSteps.put(chatId, InputStep.COUNTRY);
                    userInput.put(chatId, new HashMap<>());*/
                    fsmService.start(chatId);
                    sendMessage(chatId, "Введите страну рождения:");
                }
                case "CANCEL_BUTTON" -> {
                    /*
                    userSteps.remove(chatId);
                    userInput.remove(chatId);*/
                    fsmService.cancel(chatId);
                    sendMessage(chatId, "Ввод данных отменён ❌");
                }
                case "PREDICT_BUTTON" -> sendMessage(chatId, "Функция прогноза в разработке 🔮");
                default -> sendMessage(chatId, "Неизвестная команда.");
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        InlineKeyboardButton natalButton = new InlineKeyboardButton("Натальная карта");
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
    }//перехват ошибок AOP
    private void sendNatal(){
    }
}