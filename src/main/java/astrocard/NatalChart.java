package astrocard;

import swisseph.SweConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NatalChart {
    private Map<Integer, PlanetPosition> planetPositions;
    private Map<Integer, ZodiacSign> housePositionInZodiacSign;
    private List<Aspect> aspectList;
    private double[] houseCusps; // Cusp 1 to 12
    private double[] asmc;


    private double ascendant;
    private double midheaven;
    public NatalChart() {
        this.planetPositions = new HashMap<>();
        this.housePositionInZodiacSign = new HashMap<>();
        this.aspectList = new ArrayList<>();
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

    public Map<Integer, ZodiacSign> getHousePositionInZodiacSign() {
        return housePositionInZodiacSign;
    }

    public void setHousePositionInZodiacSign() {
        for(int i =1; i<=12; i++){
            this.housePositionInZodiacSign.put(i, ZodiacSign.fromInt((int) this.houseCusps[i] / 30));
            //ZodiacSign.fromInt((int)this.houseCusps[i]/30).getName();
        }
    }

    public List<Aspect> getAspectList() {
        return aspectList;
    }

    public void setAspectList() {
        CelestialObject[] objects = CelestialObject.values();
        for(int i =0 ; i<objects.length-1; i++){
            for (int j = i + 1; j < objects.length; j++) {
                double first = this.planetPositions.get(objects[i].getSwissephId()).getLongitude();
                double second = this.planetPositions.get(objects[j].getSwissephId()).getLongitude();
                if(Math.abs(first-second)<=188 && Math.abs(first-second)>=172){
                    this.aspectList.add(new Aspect(objects[i].getSwissephId(), objects[j].getSwissephId(), Math.abs(first-second)));
                } else if (Math.abs(first-second)<=5) {
                    this.aspectList.add(new Aspect(objects[i].getSwissephId(), objects[j].getSwissephId(), Math.abs(first-second)));
                } else if (Math.abs(first-second)<=126 && Math.abs(first-second)>=114) {
                    this.aspectList.add(new Aspect(objects[i].getSwissephId(), objects[j].getSwissephId(), Math.abs(first-second)));
                } else if (Math.abs(first-second)<=96 && Math.abs(first-second)>=84) {
                    this.aspectList.add(new Aspect(objects[i].getSwissephId(), objects[j].getSwissephId(), Math.abs(first-second)));
                } else if (Math.abs(first-second)<=64 && Math.abs(first-second)>=56) {
                    this.aspectList.add(new Aspect(objects[i].getSwissephId(), objects[j].getSwissephId(), Math.abs(first-second)));
                }
                else {
                    continue;
                }
            }
        }
    }
}
