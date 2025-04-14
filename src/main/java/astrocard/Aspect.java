package astrocard;

public class Aspect {
    private int firstPlanetId;
    private int secondPlanetId;
    private double angle;

    public Aspect(int firstPlanetId, int secondPlanetId, double angle) {
        this.firstPlanetId = firstPlanetId;
        this.secondPlanetId = secondPlanetId;
        this.angle = angle;
    }

    public int getFirstPlanetId() {
        return firstPlanetId;
    }

    public void setFirstPlanetId(int firstPlanetId) {
        this.firstPlanetId = firstPlanetId;
    }

    public int getSecondPlanetId() {
        return secondPlanetId;
    }

    public void setSecondPlanetId(int secondPlanetId) {
        this.secondPlanetId = secondPlanetId;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
