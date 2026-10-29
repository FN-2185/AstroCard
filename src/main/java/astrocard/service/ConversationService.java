package astrocard.service;

import astrocard.CelestialObject;
import astrocard.NatalChartService;
import astrocard.chart.ChartRequest;
import astrocard.entity.BotSession;
import astrocard.exception.InvalidCountryCityException;
import astrocard.repository.BotSessionRepository;
import astrocard.telegrambot.*;
import astrocard.telegrambot.eventstate.*;
import astrocard.telegrambot.verifiers.Verifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.Optional;
import static astrocard.telegrambot.BotReply.View.*;

@Service
public class ConversationService {
    private final BotSessionRepository sessions;
    private final BirthProfileService profiles;
    private final CityService cities;
    private final StateMachineService machine;
    private final NatalChartService charts;
    private final Clock clock;

    public ConversationService(BotSessionRepository sessions, BirthProfileService profiles, CityService cities,
                               StateMachineService machine, NatalChartService charts, Clock clock) {
        this.sessions = sessions; this.profiles = profiles; this.cities = cities;
        this.machine = machine; this.charts = charts; this.clock = clock;
    }


    @Transactional
    public BotReply handle(long chatId, int updateId, String text, String callback) {
        sessions.createIfAbsent(chatId);
        BotSession session = sessions.findForUpdate(chatId);
        Instant receivedAt = clock.instant();
        // Telegram may restart its update-id sequence after a week without updates.
        boolean recent = session.lastUpdateAt == null
                || session.lastUpdateAt.isAfter(receivedAt.minus(Duration.ofDays(7)));
        if (recent && session.lastUpdateId != null && updateId <= session.lastUpdateId) return BotReply.of(NONE, "");
        BotReply reply;
        try {
            reply = callback == null ? onText(session, text == null ? "" : text.trim()) : onCallback(session, callback);
        } catch (java.time.format.DateTimeParseException exception) {
            reply = BotReply.of(INPUT, "Неверный формат. " + prompt(session.state));
        } catch (InvalidCountryCityException | IllegalArgumentException exception) {
            reply = BotReply.of(INPUT, exception.getMessage() + "\n" + prompt(session.state));
        } catch (DateTimeException exception) {
            reply = BotReply.of(INPUT, "Проверьте дату, время и смещение часового пояса.");
        }
        session.lastUpdateId = updateId;
        session.lastUpdateAt = receivedAt;
        sessions.save(session);
        return reply;
    }

    private BotReply onText(BotSession session, String text) {
        String command = text.split("\\s+", 2)[0].split("@", 2)[0];
        if ("/start".equals(command)) {
            session.clearDraft();
            Optional<ChartRequest> saved = profiles.findChart(session.chatId);
            if (saved.isPresent()) {
                move(session, BotEvent.SHOW_SAVED_CHART);
                return BotReply.chart(CHART, saved.get());
            }
            move(session, BotEvent.START_COMMAND);
            return menu(session, "Выберите действие. Если карта была создана в прежней версии, введите данные заново.");
        }
        if ("/cancel".equals(command)) return cancel(session);
        switch (session.state) {
            case NATAL_COUNTRY -> {
                var country = cities.validateCountry(text);
                move(session, BotEvent.ENTERED_COUNTRY);
                session.country = country.getCountry(); session.countryCode = country.getIso();
            }
            case NATAL_CITY -> {
                var city = cities.getCoordinatesByCityAndCountry(session.country, text);
                ZoneId.of(city.getTimezone());
                if (city.getLatitude() == null || city.getLongitude() == null) {
                    throw new IllegalArgumentException("У города нет координат. Выберите другой город.");
                }
                move(session, BotEvent.ENTERED_CITY);
                session.city = city.getName(); session.timezone = city.getTimezone();
                session.latitude = city.getLatitude(); session.longitude = city.getLongitude();
            }
            case NATAL_DATE -> {
                LocalDate date = Verifier.isValidDate(text);
                if (date.isAfter(LocalDate.now(clock))) throw new IllegalArgumentException("Дата рождения не может быть в будущем.");
                move(session, BotEvent.ENTERED_DATE);
                session.date = date;
            }
            case NATAL_TIME -> {
                var moment = Verifier.resolveBirthTime(session.timezone, session.date, text);
                ChartRequest request = new ChartRequest(moment.instant(), session.latitude, session.longitude);
                charts.getChart(request);
                move(session, BotEvent.ENTERED_TIME);
                session.time = moment.localTime();
                profiles.save(session, moment.instant());
                return BotReply.chart(CHART, request);
            }
            default -> { return BotReply.of(NOTICE, "Используйте кнопки меню или команду /start."); }
        }
        return BotReply.of(INPUT, prompt(session.state));
    }

    private BotReply onCallback(BotSession session, String callback) {
        if ("CANCEL_BUTTON".equals(callback)) return cancel(session);
        if ("NATAL_BUTTON".equals(callback)) {
            if (!tryMove(session, BotEvent.SELECT_NATAL_CHART)) return stale();
            session.clearDraft();
            return BotReply.of(INPUT, prompt(session.state));
        }
        Optional<ChartRequest> saved = profiles.findChart(session.chatId);
        if (saved.isEmpty()) {
            return session.state == BotState.CHOOSE_ACTION
                    ? menu(session, "Сначала создайте натальную карту. Для старой записи нужен повторный ввод.") : stale();
        }
        ChartRequest request = saved.get();
        switch (callback) {
            case "SAVED_CHART" -> {
                move(session, BotEvent.SHOW_SAVED_CHART);
                return BotReply.chart(CHART, request);
            }
            case "PREDICT_BUTTON" -> {
                if (!tryMove(session, BotEvent.SELECT_FORECAST)) return stale();

                Instant now = clock.instant().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
                return BotReply.chart(FORECAST, new ChartRequest(now, request.latitude(), request.longitude()));
            }
            case "PLANETS_IN_SIGNS_BUTTON" -> {
                if (!tryMove(session, BotEvent.SELECT_DESCRIPTION_PLANET_IN_SIGN_MENU)) return stale();
                return BotReply.of(PLANETS_SIGN, "Выберите планету в знаке:");
            }
            case "PLANETS_IN_HOUSES_BUTTON" -> {
                if (!tryMove(session, BotEvent.SELECT_DESCRIPTION_PLANET_IN_HOUSE_MENU)) return stale();
                return BotReply.of(PLANETS_HOUSE, "Выберите планету в доме:");
            }
            case "BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET" -> {
                if (!tryMove(session, BotEvent.BACK_TO_PLANETS_FROM_DESCRIPTION_OF_PLANET)) return stale();
                return BotReply.of(session.state == BotState.CHOOSE_DESCRIPTION_PLANET_IN_SIGN_MENU
                        ? PLANETS_SIGN : PLANETS_HOUSE, "Выберите планету:");
            }
            case "BACK_TO_NATAL_FROM_DESCRIPTION" -> {
                if (!tryMove(session, BotEvent.BACK_TO_NATAL_RESULT)) return stale();
                return BotReply.chart(CHART, request);
            }
            default -> {
                CelestialObject planet;
                try { planet = CelestialObject.valueOf(callback); }
                catch (IllegalArgumentException exception) { return stale(); }
                if (!tryMove(session, BotEvent.SELECT_PLANET)) return stale();
                return new BotReply(session.state == BotState.SHOW_PLANET_IN_SIGN
                        ? DESCRIPTION_SIGN : DESCRIPTION_HOUSE, "", request, planet);
            }
        }
    }

    private BotReply cancel(BotSession session) {
        move(session, BotEvent.BACK_TO_START);
        session.clearDraft();
        return menu(session, "Выберите действие:");
    }
    private BotReply menu(BotSession session, String text) {
        return new BotReply(MENU, text, profiles.findChart(session.chatId).orElse(null), null);
    }
    private boolean tryMove(BotSession session, BotEvent event) {
        var target = machine.transition(session.state, event);
        target.ifPresent(state -> session.state = state);
        return target.isPresent();
    }
    private void move(BotSession session, BotEvent event) {
        if (!tryMove(session, event)) throw new IllegalStateException("Rejected event " + event + " in " + session.state);
    }
    private BotReply stale() { return BotReply.of(NOTICE, "Эта кнопка сейчас недоступна. Используйте последнее меню или /start."); }
    private String prompt(BotState state) {
        return switch (state) {
            case NATAL_COUNTRY -> "Введите страну рождения:";
            case NATAL_CITY -> "Введите город рождения:";
            case NATAL_DATE -> "Введите дату рождения (ДД.ММ.ГГГГ):";
            case NATAL_TIME -> "Введите местное время рождения (ЧЧ:ММ):";
            default -> "Используйте /start для возврата к карте или меню.";
        };
    }
}
