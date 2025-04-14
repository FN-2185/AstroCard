package astrocard;

import swisseph.SweConst;

import java.util.HashMap;
import java.util.Map;

public class NatalChart {
    private Map<Integer, PlanetPosition> planetPositions;
    private double[] houseCusps; // Cusp 1 to 12
    private double[] asmc;


    private double ascendant;
    private double midheaven;
    public NatalChart() {
        this.planetPositions = new HashMap<>();
        //this.houseCusps = new double[13];
    }
    public Map<Integer, PlanetPosition> getPlanetPositions() {
        return planetPositions;
    }

    public void setPlanetPositions(Map<Integer, PlanetPosition> planetPositions) {
        this.planetPositions = planetPositions;
    }

    public double[] getHouseCusps() {
        return houseCusps;
    }

    public void setHouseCusps(double[] houseCusps) {
        this.houseCusps = houseCusps;
    }

    public double getAscendant() {
        return ascendant;
    }

    public void setAscendant(double ascendant) {
        this.ascendant = ascendant;
    }

    public double getMidheaven() {
        return midheaven;
    }

    public void setMidheaven(double midheaven) {
        this.midheaven = midheaven;
    }
    public double[] getAsmc() {
        return asmc;
    }

    public void setAsmc(double[] asmc) {
        this.asmc = asmc;
    }

}
