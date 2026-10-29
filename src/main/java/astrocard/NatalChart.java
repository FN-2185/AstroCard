package astrocard;

import java.util.*;


public final class NatalChart {
    private final Map<Integer, PlanetPosition> planetPositions;
    private final Map<Integer, String> housePositionInZodiacSign;
    private final List<Aspect> aspects;
    private final double[] houseCusps;
    private final double[] ascmc;

    public NatalChart(Map<Integer, PlanetPosition> positions, double[] cusps, double[] ascmc) {
        if (cusps.length != 13 || ascmc.length < 2) throw new IllegalArgumentException("Invalid houses");
        this.planetPositions = Collections.unmodifiableMap(new LinkedHashMap<>(positions));
        this.houseCusps = cusps.clone();
        this.ascmc = ascmc.clone();
        Map<Integer, String> signs = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            signs.put(i, ZodiacSign.fromInt((int) (PlanetPosition.normalize(cusps[i]) / 30)).getName());
        }
        this.housePositionInZodiacSign = Collections.unmodifiableMap(signs);
        List<PlanetPosition> planets = new ArrayList<>(positions.values());
        List<Aspect> calculated = new ArrayList<>();
        for (int i = 0; i < planets.size(); i++) {
            for (int j = i + 1; j < planets.size(); j++) {
                PlanetPosition a = planets.get(i), b = planets.get(j);
                double angle = angularDistance(a.getLongitude(), b.getLongitude());
                if (angle <= 5 || Math.abs(angle - 60) <= 4 || Math.abs(angle - 90) <= 6
                        || Math.abs(angle - 120) <= 6 || Math.abs(angle - 180) <= 8) {
                    calculated.add(new Aspect(a.getPlanet_id(), b.getPlanet_id(), angle));
                }
            }
        }
        this.aspects = List.copyOf(calculated);
    }

    public static double angularDistance(double a, double b) {
        double difference = Math.abs(PlanetPosition.normalize(a) - PlanetPosition.normalize(b));
        return Math.min(difference, 360 - difference);
    }

    public Map<Integer, PlanetPosition> getPlanetPositions() { return planetPositions; }
    public Map<Integer, String> getHousePositionInZodiacSign() { return housePositionInZodiacSign; }
    public List<Aspect> getAspectList() { return aspects; }
    public double[] getHouseCusps() { return houseCusps.clone(); }
    public double[] getAsmc() { return ascmc.clone(); }
    public double getAscendant() { return ascmc[0]; }
    public double getMidheaven() { return ascmc[1]; }
}
