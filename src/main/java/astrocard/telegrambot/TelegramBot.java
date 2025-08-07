package astrocard.telegrambot;

import astrocard.CelestialObject;
import astrocard.config.BotConfig;
import astrocard.entity.Cities;
import astrocard.entity.Descriptions;
import astrocard.exception.InvalidCountryCityException;
import astrocard.service.CityService;
import astrocard.service.DescriptionSenderService;
import astrocard.telegrambot.config.CacheConfig;
import astrocard.telegrambot.eventstate.BotEvent;
import astrocard.telegrambot.eventstate.BotState;
import astrocard.telegrambot.natalchartsender.ChartSender;
import astrocard.telegrambot.payload.NatalData;
import astrocard.telegrambot.telegrambotmessagesender.TelegramBotMessageSender;
import astrocard.telegrambot.verifiers.Verifier;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Import(CacheConfig.class)

@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final CityService cityService;
    private final BotConfig botConfig;
    private final TelegramClient telegramClient;
    private StateMachineService stateMachineService;
    private DescriptionSenderService descriptionSenderService;
    
    private final Cache<Long, NatalData> natalDataCache;
    private final Cache<Long, List<Descriptions>> descriptionCache;

    public TelegramBot(BotConfig botConfig, StateMachineService stateMachineService,
                       CityService cityService, DescriptionSenderService descriptionSenderService,
                       Cache<Long, NatalData> natalDataCache, Cache<Long, List<Descriptions>> descriptionCache) {
        this.botConfig = botConfig;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.stateMachineService=stateMachineService;
        this.cityService=cityService;
        this.descriptionSenderService=descriptionSenderService;
        this.natalDataCache=natalDataCache;
        this.descriptionCache=descriptionCache;
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
        StateMachine<BotState, BotEvent> sm;
        BotState state;
        if(update.hasMessage() && update.getMessage().hasText() || update.hasCallbackQuery()){
            long chatId = extractChatId(update);
            if(natalDataCache.getIfPresent(chatId)==null){
                Optional<NatalData> user = this.cityService.userExists(chatId);
                if (user.isPresent()){
                    natalDataCache.put(chatId, user.get());
                    if(List.of(BotState.NATAL_RESULT, BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU,
                            BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU, BotState.CHOOSE_PLANET).contains(user.get().getFsm())){
                        descriptionCache.put(chatId, this.descriptionSenderService.getDescriptions(user.get()));
                    }
                }
            }
        }



        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            sm = loadSM(chatId);
            state = sm.getState().getId();
            if(text.equals("/start") && List.of(BotState.NATAL_RESULT,
                    BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU,
                    BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU,
                    BotState.CHOOSE_PLANET, BotState.FORECAST_CARD).contains(state)){
                natalDataCache.put(chatId, this.cityService.userExists(chatId).get());
                fire(sm, BotEvent.ENTERED_TIME, chatId);
                this.descriptionCache.put(chatId, this.descriptionSenderService.getDescriptions(natalDataCache.getIfPresent(chatId)));
                TelegramBotMessageSender.sendPlanetsSignsHousesMenuMessage(chatId, ChartSender.sendChart(natalDataCache.getIfPresent(chatId)), telegramClient, descriptionCache.getIfPresent(chatId));
            }

            else if (text.equals("/start")) {
                sm.startReactively().block();
                fire(sm, BotEvent.START_COMMAND, chatId);
                TelegramBotMessageSender.sendDefaultMessage(chatId, "Привет! Выбери, с чего начать:", telegramClient);
                return;
            }
            if(List.of(BotState.NATAL_COUNTRY, BotState.NATAL_CITY,
                    BotState.NATAL_DATE, BotState.NATAL_TIME).contains(state)){
                handleInput(chatId, text);
            }
            //handleInput(chatId, text);
        }
        else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            sm = loadSM(chatId);
            state = sm.getState().getId();

            if(Objects.equals(callBackData, "NATAL_BUTTON") && state==BotState.CHOOSE_ACTION){
                /*Optional<NatalData> natalData = this.cityService.userExists(chatId);
                if(natalData.isPresent()){
                    natalDataCache.put(chatId, natalData.get());
                    fire(sm, BotEvent.ENTERED_TIME, chatId);
                    this.descriptionCache.put(chatId, this.descriptionSenderService.getDescriptions(natalDataCache.getIfPresent(chatId)));
                    TelegramBotMessageSender.sendPlanetsSignsHousesMenuMessage(chatId, ChartSender.sendChart(natalDataCache.getIfPresent(chatId)), telegramClient, descriptionCache.getIfPresent(chatId));
                }else {*/
                    fire(sm, BotEvent.SELECT_NATAL_CHART, chatId);
                    natalDataCache.put(chatId, new NatalData());
                    natalDataCache.getIfPresent(chatId).setChatID(chatId);
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Введите страну рождения:",telegramClient);
                //}
            }
            else if(Objects.equals(callBackData, "CANCEL_BUTTON")){
                fire(sm, BotEvent.BACK_TO_START, chatId);
                TelegramBotMessageSender.sendDefaultMessage(chatId, "Ввод данных отменён ❌",telegramClient);
            }
            else if(Objects.equals(callBackData, "PREDICT_BUTTON")){
                LocalTime natalData = this.cityService.userExists(chatId).get().getTime();
                if(natalData!=null){
                    fire(sm, BotEvent.SELECT_FORECAST, chatId);
                    TelegramBotMessageSender.sendForecastMessage(chatId, ChartSender.sendForecast(natalDataCache.getIfPresent(chatId)), telegramClient);
                }
                else {
                    fire(sm, BotEvent.BACK_TO_START, chatId);
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "У вас нет карты", telegramClient);
                }

            }


            else if(Objects.equals(callBackData, "PLANETS_IN_SIGNS_BUTTON") && state==BotState.NATAL_RESULT){
                fire(sm, BotEvent.SELECT_DESCRIPTION_PLANET_IN_SIGN_MENU, chatId);
                TelegramBotMessageSender.sendPlanetsInSignsMessage(chatId, "Выберите планету", telegramClient);
            }
            else if(Objects.equals(callBackData, "PLANETS_IN_HOUSES_BUTTON") && state==BotState.NATAL_RESULT){
                fire(sm, BotEvent.SELECT_DESCRIPTION_PLANET_IN_HOUSE_MENU, chatId);
                TelegramBotMessageSender.sendPlanetsInSignsMessage(chatId, "Выберите планету", telegramClient);
            }
            else if(Objects.equals(callBackData, "SUN") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.SUN.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "MOON") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state, CelestialObject.MOON.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "MERCURY") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.MERCURY.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "VENUS") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.VENUS.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "MARS") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.MARS.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "JUPITER") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.JUPITER.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "SATURN") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.SATURN.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "URANUS") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.URANUS.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "NEPTUNE") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.NEPTUNE.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "PLUTO") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state, CelestialObject.PLUTO.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "CHIRON") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU)){
                fire(sm, BotEvent.SELECT_PLANET, chatId);
                TelegramBotMessageSender.sendOnePlanetInSign(chatId, state,CelestialObject.CHIRON.getName(), descriptionCache.getIfPresent(chatId), telegramClient);
            }
            else if(Objects.equals(callBackData, "BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET") && state==BotState.CHOOSE_PLANET){
                fire(sm, BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET ,chatId);
                TelegramBotMessageSender.sendPlanetsInSignsMessage(chatId, "Выберите планету", telegramClient);
            }
            else if(Objects.equals(callBackData, "BACK_TO_NATAL_FROM_DESCRIPTION") && (state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_HOUSE_MENU || state==BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU || state==BotState.CHOOSE_PLANET || state==BotState.FORECAST_CARD)){
                fire(sm, BotEvent.BACK_TO_NATAL_RESULT ,chatId);
                TelegramBotMessageSender.sendPlanetsSignsHousesMenuMessage(chatId, ChartSender.sendChart(natalDataCache.getIfPresent(chatId)), telegramClient, descriptionCache.getIfPresent(chatId));
            }
            else {
                TelegramBotMessageSender.sendYesNoMessage(chatId, "Используйте кнопки последнего сообщения.", telegramClient);
            }
        }
    }
    private void handleInput(long chatId, String text) {
        BotState state;
        StateMachine<BotState, BotEvent> sm;
        sm = loadSM(chatId);
        state = sm.getState().getId();
        switch (state) {
            case NATAL_COUNTRY -> {
                try {
                    ArrayList<String> list= cityService.validateCountry(text);
                    String country = list.get(0);
                    String country_code = list.get(1);
                    natalDataCache.getIfPresent(chatId).setCountry(country);
                    natalDataCache.getIfPresent(chatId).setCountryCode(country_code);
                    fire(sm, BotEvent.ENTERED_COUNTRY, chatId);
                    state = sm.getState().getId();
                    this.cityService.saveUserCountry(chatId, country, state);
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Введите город рождения:",telegramClient);
                }catch (InvalidCountryCityException e) {
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Error"+ e.getMessage(),telegramClient);
                }
            }
            case NATAL_CITY -> {
                try {
                    Cities res = cityService.getCoordinatesByCityAndCountry(natalDataCache.getIfPresent(chatId).getCountry(), text);
                    natalDataCache.getIfPresent(chatId).setCity(res.getName());
                    natalDataCache.getIfPresent(chatId).setLatitude(res.getLatitude());
                    natalDataCache.getIfPresent(chatId).setLongitude(res.getLongitude());
                    natalDataCache.getIfPresent(chatId).setTimezone(res.getTimezone());
                    fire(sm, BotEvent.ENTERED_CITY, chatId);
                    state = sm.getState().getId();
                    this.cityService.saveUserCity(chatId, res.getName(), state);
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Введите дату рождения (дд.мм.гггг):",telegramClient);
                } catch (InvalidCountryCityException e) {
                    TelegramBotMessageSender.sendDefaultMessage(chatId, e.getMessage(),telegramClient);
                }
            }
            case NATAL_DATE -> {
                try {
                    LocalDate date = Verifier.isValidDate(text);
                    natalDataCache.getIfPresent(chatId).setDate(date);
                    fire(sm, BotEvent.ENTERED_DATE, chatId);
                    state = sm.getState().getId();
                    this.cityService.saveUserDate(chatId, date, state);
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Введите время рождения (чч:мм):",telegramClient);

                }catch (DateTimeParseException e){
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Неверный формат даты",telegramClient);
                }
            }
            case NATAL_TIME -> {
                try{
                    LocalTime time = Verifier.isValidTime(text);
                    natalDataCache.getIfPresent(chatId).setTime(time);
                    fire(sm, BotEvent.ENTERED_TIME, chatId);
                    ZonedDateTime dateTime= Verifier.timeToUtc(natalDataCache.getIfPresent(chatId).getTimezone(), natalDataCache.getIfPresent(chatId).getDate(), natalDataCache.getIfPresent(chatId).getTime());
                    natalDataCache.getIfPresent(chatId).setDate(dateTime.toLocalDate());
                    natalDataCache.getIfPresent(chatId).setTime(dateTime.toLocalTime());
                    this.descriptionCache.put(chatId, this.descriptionSenderService.getDescriptions(natalDataCache.getIfPresent(chatId)));
                    TelegramBotMessageSender.sendPlanetsSignsHousesMenuMessage(chatId, ChartSender.sendChart(natalDataCache.getIfPresent(chatId)), telegramClient, descriptionCache.getIfPresent(chatId));
                    state = sm.getState().getId();
                    this.cityService.saveUserData(natalDataCache.getIfPresent(chatId), state);
                }
                catch (DateTimeParseException e){
                    TelegramBotMessageSender.sendDefaultMessage(chatId, "Неверный формат времени",telegramClient);
                }
            }

            default -> TelegramBotMessageSender.sendDefaultMessage(chatId, "Пожалуйста, используйте кнопки меню.", telegramClient);
        }
    }
    private StateMachine<BotState,BotEvent> loadSM(long chatId) {
        try {
            return stateMachineService.restoreStateMachine(chatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void persistSM(StateMachine<BotState, BotEvent> sm, long chatId) {
        try {
            stateMachineService.persistStateMachine(sm, chatId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void fire(StateMachine<BotState,BotEvent> sm, BotEvent event, long chatId) {
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).blockLast();
        persistSM(sm, chatId);
    }
    public static Long extractChatId(Update update) {
        if(update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        else  {
            return update.getMessage().getChatId();
        }
    }
}

