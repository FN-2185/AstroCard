package astrocard.telegrambot.payload;

import astrocard.telegrambot.eventstate.BotState;

import java.time.LocalDate;
import java.time.LocalTime;

public class NatalData {
    private Long chatID;
    private String country;
    private String countryCode;
    private String timezone;
    private String city;
    private LocalDate date;
    private LocalTime time;
    private Double latitude;
    private Double longitude;
    private BotState fsm;

    public NatalData(){}

    public NatalData(Long chatID, String country,String countryCode, String timezone,  String city, LocalDate date, LocalTime time, Double latitude, Double longitude) {
        this.chatID=chatID;
        this.country = country;
        this.countryCode=countryCode;
        this.timezone=timezone;
        this.city = city;
        this.date = date;
        this.time = time;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public NatalData(Long chatID, String country,String countryCode,  String city, LocalDate date, LocalTime time, Double latitude, Double longitude, BotState fsm) {
        this.chatID=chatID;
        this.country = country;
        this.countryCode=countryCode;
        this.city = city;
        this.date = date;
        this.time = time;
        this.latitude=latitude;
        this.longitude=longitude;
        this.fsm=fsm;
    }

    public Long getChatID() {
        return chatID;
    }

    public void setChatID(Long chatID) {
        this.chatID = chatID;
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

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

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BotState getFsm() {
        return fsm;
    }

    public void setFsm(BotState fsm) {
        this.fsm = fsm;
    }

    @Override
    public String toString() {
        return String.format("Страна: %s\nГород: %s\nДата: %s\nВремя: %s", country, city, date, time);
    }
}
