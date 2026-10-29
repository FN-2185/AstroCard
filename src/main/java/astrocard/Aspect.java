package astrocard;

public record Aspect(int firstPlanetId, int secondPlanetId, double angle) {
    public int getFirstPlanetId() { return firstPlanetId; }
    public int getSecondPlanetId() { return secondPlanetId; }
    public double getAngle() { return angle; }
}
