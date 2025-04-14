package astrocard;
import swisseph.SweConst;
public enum CelestialObject {
    SUN(SweConst.SE_SUN),
    MOON(SweConst.SE_MOON),
    MERCURY(SweConst.SE_MERCURY),
    VENUS(SweConst.SE_VENUS),
    MARS(SweConst.SE_MARS),
    JUPITER(SweConst.SE_JUPITER),
    SATURN(SweConst.SE_SATURN),
    URANUS(SweConst.SE_URANUS),
    NEPTUNE(SweConst.SE_NEPTUNE),
    PLUTO(SweConst.SE_PLUTO),
    CHIRON(SweConst.SE_CHIRON),
    MEAN_APOGEE(SweConst.SE_MEAN_APOG),
    WHITE_MOON(SweConst.SE_WHITE_MOON); // неофициальное, если ты его используешь

    private final int swissephId;

    CelestialObject(int swissephId) {
        this.swissephId = swissephId;
    }

    public int getSwissephId() {
        return swissephId;
    }
}
