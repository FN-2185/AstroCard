package astrocard.telegrambot;

import astrocard.NatalChart;
import astrocard.NatalChartService;
import astrocard.PlanetPosition;
import astrocard.config.BotConfig;
import astrocard.entity.Cities;
import astrocard.exception.InvalidCountryCityException;
import astrocard.service.CityService;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
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
import reactor.core.publisher.Mono;
import swisseph.SweDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final CityService cityService;
    private final BotConfig botConfig;
    private final TelegramClient telegramClient;
    private StateMachineService stateMachineService;

    private final Map<Long, Map<String, String>> userInput = new ConcurrentHashMap<>();

    public TelegramBot(BotConfig botConfig, StateMachineService stateMachineService, CityService cityService) {
        this.botConfig = botConfig;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.stateMachineService=stateMachineService;
        this.cityService=cityService;
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
        BotState state;
        StateMachine<BotState, BotEvent> sm;
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            try {
                sm = stateMachineService.restoreStateMachine(chatId);
                state = sm.getState().getId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (text.equals("/start")) {
                sm.startReactively().block();
                sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.START_COMMAND).build())).blockLast();
                sendMessage(chatId, "Привет! Выбери, с чего начать:");
                try {
                    stateMachineService.persistStateMachine(sm, chatId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            handleInput(chatId, text);
        }
        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            try {
                sm = stateMachineService.restoreStateMachine(chatId);
                state = sm.getState().getId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            switch (callBackData) {
                case "NATAL_BUTTON" -> {//когда нажимаю эту кнопку в любой момент дожен возвращаться к сюда
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.SELECT_NATAL_CHART).build())).blockLast();
                    userInput.put(chatId, new HashMap<>());
                    sendMessage(chatId, "Введите страну рождения:");
                    try {
                        stateMachineService.persistStateMachine(sm, chatId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "CANCEL_BUTTON" -> {
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.BACK_TO_START).build())).blockLast();
                    sendMessage(chatId, "Ввод данных отменён ❌");
                    try {
                        stateMachineService.persistStateMachine(sm, chatId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                case "PREDICT_BUTTON" -> {
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.SELECT_FORECAST).build())).blockLast();
                    sendMessage(chatId, "У вас уже есть натальная карта? (введите Да или Нет)");
                    try {
                        stateMachineService.persistStateMachine(sm, chatId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> sendMessage(chatId, "Неизвестная команда.");
            }
        }
    }
    private void handleInput(long chatId, String text) {
        BotState state;
        StateMachine<BotState, BotEvent> sm;
        try {
            sm = stateMachineService.restoreStateMachine(chatId);
            state = sm.getState().getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        switch (state) {
            case NATAL_COUNTRY -> {
                try {
                    ArrayList<String> list= cityService.validateCountry(text);
                    String country = list.get(0);
                    String country_code = list.get(1);
                    userInput.get(chatId).put("country", country);
                    userInput.get(chatId).put("country_code", country_code);
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.ENTERED_COUNTRY).build())).blockLast();
                    sendMessage(chatId, "Введите город рождения:");
                    stateMachineService.persistStateMachine(sm, chatId);
                }catch (InvalidCountryCityException e) {
                    sendMessage(chatId, "Error");
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case NATAL_CITY -> {
                Cities res = cityService.getCoordinatesByCityAndCountry(userInput.get(chatId).get("country") , text);
                userInput.get(chatId).put("city", res.getName());
                userInput.get(chatId).put("latitude",res.getLatitude().toString());
                userInput.get(chatId).put("longitude", res.getLongitude().toString());
                sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.ENTERED_CITY).build())).blockLast();
                sendMessage(chatId, "Введите дату рождения (дд.мм.гггг):");
                try {
                    stateMachineService.persistStateMachine(sm, chatId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case NATAL_DATE -> {
                userInput.get(chatId).put("date", text);
                sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.ENTERED_DATE).build())).blockLast();
                sendMessage(chatId, "Введите время рождения (чч:мм):");
                try {
                    stateMachineService.persistStateMachine(sm, chatId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case NATAL_TIME -> {
                userInput.get(chatId).put("time", text);
                sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.ENTERED_TIME).build())).blockLast();
                sendMessage(chatId, "Вот ваша натальная карта! "+userInput.get(chatId).get("country")+" "+userInput.get(chatId).get("city")
                        +" "+userInput.get(chatId).get("longitude"));
                sendMessage(chatId, "Что хотите сделать дальше?");
                try {
                    stateMachineService.persistStateMachine(sm, chatId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            case FORECAST_CHECK_CARD -> {
                if (text.equalsIgnoreCase("да")) {
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.USER_HAS_CARD).build())).blockLast();
                    sendMessage(chatId, "Функция прогноза в разработке 🔮");
                    sendMessage(chatId, "Вернуться в главное меню:");
                    try {
                        stateMachineService.persistStateMachine(sm, chatId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    sm.sendEvent(Mono.just(MessageBuilder.withPayload(BotEvent.USER_HAS_NO_CARD).build())).blockLast();
                    sendMessage(chatId, "Создадим натальную карту. Введите страну рождения:");
                    try {
                        stateMachineService.persistStateMachine(sm, chatId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            default -> sendMessage(chatId, "Пожалуйста, используйте кнопки меню.");
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
    }




/*
    //перехват ошибок AOP
    private void sendNatal(){
    }


    private StringBuilder getCard(NatalData data){

        StringBuilder card = new StringBuilder();
        card.append("<b>Натальная карта:</b>\n\n");
        String country = translateCountryToEnglish(data.getCountry());
        String city = data.getCity();
        String[] parts = data.getDate().split("\\.");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        parts = data.getTime().split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        Cities cities= this.cityService.getCoordinatesByCityAndCountry(country, city);
        double latitude = cities.getLatitude();
        double longitude = cities.getLongitude();
        SweDate sweDate = new SweDate(year, month,day, hour+(minute/60.0), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), latitude, longitude);
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            PlanetPosition pos = entry.getValue();
            card.append("<b>").append(pos.getPlanet_name()).append("</b>: ")
                    .append("<i>").append(pos.getSignName()).append("</i> ")
                    .append(pos.getDegreesInSign()).append("° ")
                    .append(pos.getMinutesInSign()).append("'\n");
        }
        return card;
    }*/
}