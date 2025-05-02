package astrocard.telegrambot;

public class NatalData {
    private String country;
    private String city;
    private String date;
    private String time;
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    @Override
    public String toString() {
        return String.format("Страна: %s\nГород: %s\nДата: %s\nВремя: %s", country, city, date, time);
    }
}
