package astrocard;


public final class PlanetPosition {
    private final int planetId;
    private final double[] values;
    private final int houseNumber;

    public PlanetPosition(int planetId, double[] values, double[] houseCusps) {
        if (values == null || values.length != 6) throw new IllegalArgumentException("Six coordinates required");
        for (double value : values) {
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Non-finite planet coordinate");
        }
        CelestialObject.fromSwissephId(planetId);
        this.planetId = planetId;
        this.values = values.clone();
        this.values[0] = normalize(values[0]);
        this.houseNumber = locateHouse(this.values[0], houseCusps);
    }

    public static double normalize(double angle) {
        if (!Double.isFinite(angle)) throw new IllegalArgumentException("Non-finite angle");
        return ((angle % 360) + 360) % 360;
    }

    public static int locateHouse(double longitude, double[] cusps) {
        if (cusps == null || cusps.length != 13) throw new IllegalArgumentException("Twelve house cusps required");
        double position = normalize(longitude);
        for (int i = 1; i <= 12; i++) {
            double start = normalize(cusps[i]);
            double end = normalize(cusps[i == 12 ? 1 : i + 1]);
            if (normalize(position - start) < normalize(end - start)) return i;
        }
        throw new IllegalArgumentException("Invalid house cusps");
    }

    public int getPlanet_id() { return planetId; }
    public String getPlanet_name() { return CelestialObject.fromSwissephId(planetId).getName(); }
    public double getLongitude() { return values[0]; }
    public double getLatitude() { return values[1]; }
    public double getDistance() { return values[2]; }
    public double getSpeedLongitude() { return values[3]; }
    public double getSpeedLatitude() { return values[4]; }
    public double getSpeedDistance() { return values[5]; }
    public int getSignIndex() { return (int) (getLongitude() / 30); }
    public String getSignName() { return ZodiacSign.fromInt(getSignIndex()).getName(); }
    public int getDegreesInSign() { return (int) (getLongitude() % 30); }
    public int getMinutesInSign() { return (int) ((getLongitude() % 1) * 60); }
    public int getSecondsInSign() { return (int) ((((getLongitude() % 1) * 60) % 1) * 60); }
    public int getHouseNumber() { return houseNumber; }
}
