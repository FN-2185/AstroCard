package astrocard;
import swisseph.SweConst;
public enum CelestialObject {
    SUN(SweConst.SE_SUN, "Солнце"),
    MOON(SweConst.SE_MOON, "Луна"),
    MERCURY(SweConst.SE_MERCURY, "Меркурий"),
    VENUS(SweConst.SE_VENUS, "Венера"),
    MARS(SweConst.SE_MARS, "Марс"),
    JUPITER(SweConst.SE_JUPITER, "Юпитер"),
    SATURN(SweConst.SE_SATURN, "Сатурн"),
    URANUS(SweConst.SE_URANUS, "Уран"),
    NEPTUNE(SweConst.SE_NEPTUNE, "Нептун"),
    PLUTO(SweConst.SE_PLUTO, "Плутон"),
    CHIRON(SweConst.SE_CHIRON, "Хирон");
    //MEAN_APOGEE(SweConst.SE_MEAN_APOG, "Черная Луна");
    //WHITE_MOON(SweConst.SE_WHITE_MOON, "Белая Луна");

    private final int swissephId;
    private final String name;

    CelestialObject(int swissephId, String name) {
        this.swissephId = swissephId;
        this.name=name;
    }

    public int getSwissephId() {
        return swissephId;
    }
    public String getName() {
        return name;
    }
    public static CelestialObject fromSwissephId(int id) {
        for (CelestialObject obj : values()) {
            if (obj.getSwissephId() == id) {
                return obj;
            }
        }
        throw new IllegalArgumentException("Неизвестный ID планеты: " + id);
    }
}
