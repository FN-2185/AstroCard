package astrocard.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class UserData {
    private Long chatID;
    private String country;
    private String city;

    private LocalDate birthDate;
    private LocalTime birthTime;

    private Double latitude;
    private Double longitude;
    public UserData(){}

    public UserData(Long chatID, String country, String city, LocalDate birthDate, LocalTime birthTime, Double latitude, Double longitude) {
        this.chatID = chatID;
        this.country = country;
        this.city = city;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
