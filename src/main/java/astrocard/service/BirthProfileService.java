package astrocard.service;

import astrocard.chart.ChartRequest;
import astrocard.entity.*;
import astrocard.repository.UserDataRepository;
import astrocard.telegrambot.eventstate.BotState;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.Optional;

@Service
public class BirthProfileService {
    private final UserDataRepository users;
    public BirthProfileService(UserDataRepository users) { this.users = users; }

    public Optional<ChartRequest> findChart(long chatId) {
        return users.findById(chatId).filter(user -> user.getBirthInstant() != null
                        && user.getLatitude() != null && user.getLongitude() != null)
                .map(user -> new ChartRequest(user.getBirthInstant(), user.getLatitude(), user.getLongitude()));
    }


    public void save(BotSession draft, Instant instant) {
        UserData user = users.findById(draft.chatId).orElseGet(UserData::new);
        user.setChatID(draft.chatId);
        user.setCountry(draft.country);
        user.setCountry_code(draft.countryCode);
        user.setCity(draft.city);
        user.setLatitude(draft.latitude);
        user.setLongitude(draft.longitude);
        user.setTimezone(draft.timezone);
        user.setBirthInstant(instant);

        var utc = instant.atOffset(ZoneOffset.UTC);
        user.setBirthDate(utc.toLocalDate());
        user.setBirthTime(utc.toLocalTime());
        user.setFsm(BotState.NATAL_RESULT);
        users.save(user);
    }
}
