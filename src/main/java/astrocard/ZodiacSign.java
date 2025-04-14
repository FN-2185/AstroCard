package astrocard;

public enum ZodiacSign {
    ARIES("Овен"),
    TAURUS("Телец"),
    GEMINI("Близнецы"),
    CANCER("Рак"),
    LEO("Лев"),
    VIRGO("Дева"),
    LIBRA("Весы"),
    SCORPIO("Скорпион"),
    SAGITTARIUS("Стрелец"),
    CAPRICORN("Козерог"),
    AQUARIUS("Водолей"),
    PISCES("Рыбы");

    private final String name;

    ZodiacSign(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ZodiacSign fromInt(int index) {
        return values()[index];
    }
}
