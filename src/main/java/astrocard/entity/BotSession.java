package astrocard.entity;

import astrocard.telegrambot.eventstate.BotState;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;


@Entity
@Table(name = "bot_sessions")
public class BotSession {
    @Id @Column(name = "chat_id") public Long chatId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) public BotState state = BotState.CHOOSE_ACTION;
    public String country;
    @Column(name = "country_code") public String countryCode;
    public String city;
    public String timezone;
    public Double latitude;
    public Double longitude;
    @Column(name = "birth_date") public LocalDate date;
    @Column(name = "birth_time") public LocalTime time;
    @Column(name = "last_update_id") public Integer lastUpdateId;
    @Column(name = "last_update_at") public Instant lastUpdateAt;
    @Version public Long version;

    public BotSession() {}
    public BotSession(long chatId) { this.chatId = chatId; }
    public void clearDraft() {
        country = null; countryCode = null; city = null; timezone = null;
        latitude = null; longitude = null; date = null; time = null;
    }
}
