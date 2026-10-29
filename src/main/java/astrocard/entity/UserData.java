package astrocard.entity;

import astrocard.telegrambot.eventstate.BotState;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;


@Entity
@Table(name = "users")
public class UserData {
    @Id
    @Column(name = "chat_id")
    private Long chatID;
    @Column(name = "country")
    private String country;
    @Column(name = "country_code")
    private String country_code;
    @Column(name = "city")
    private String city;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "birth_time")
    private LocalTime birthTime;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "fsm")
    @Enumerated(EnumType.STRING)
    private BotState fsm;
    @Column(name = "timezone")
    private String timezone;
    @Column(name = "birth_instant")
    private Instant birthInstant;
    @Version
    private Long version;

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public Instant getBirthInstant() { return birthInstant; }
    public void setBirthInstant(Instant birthInstant) { this.birthInstant = birthInstant; }
    public UserData(){}

    public UserData(Long chatID, BotState fsm){
        this.chatID=chatID;
        this.fsm=fsm;
    }

    public UserData(Long chatID, String country, String country_code, String city, LocalDate birthDate, LocalTime birthTime, Double latitude, Double longitude) {
        this.chatID = chatID;
        this.country = country;
        this.country_code = country_code;
        this.city = city;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserData(Long chatID, String country, String country_code, String city, LocalDate birthDate, LocalTime birthTime, Double latitude, Double longitude, BotState fsm) {
        this.chatID = chatID;
        this.country = country;
        this.country_code=country_code;
        this.city = city;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fsm=fsm;
    }

    public Long getChatID() {
        return chatID;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalTime getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(LocalTime birthTime) {
        this.birthTime = birthTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public BotState getFsm() {
        return fsm;
    }

    public void setFsm(BotState fsm) {
        this.fsm = fsm;
    }
}
