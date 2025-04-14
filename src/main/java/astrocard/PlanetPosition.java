package astrocard;

import swisseph.SwissEph;

public class PlanetPosition {
    private int planet_id;
    private String planet_name;

    private double longitude;
    private double latitude;
    private double distance;


    private double speedLongitude;
    private double speedLatitude;
    private double speedDistance;
    private int signIndex;
    private String signName;
    private int degreesInSign;
    private int minutesInSign;
    private int secondsInSign;
    private int houseNumber;
    public PlanetPosition(){}
    public PlanetPosition(int planet_id, double[] xx, double[] houseCusps) {
        this.planet_id=planet_id;
        this.planet_name= new  SwissEph().swe_get_planet_name(planet_id);
        this.longitude = xx[0];
        this.latitude = xx[1];
        this.distance = xx[2];
        this.speedLongitude = xx[3];
        this.speedLatitude = xx[4];
        this.speedDistance = xx[5];
        this.signIndex=(int)(longitude/30);
        this.signName= ZodiacSign.fromInt(signIndex).getName();
        this.degreesInSign = (int) (longitude % 30);
        this.minutesInSign = (int) ((longitude % 1) * 60);
        this.secondsInSign = (int) ((((longitude % 1) * 60) % 1) * 60);
        for(int i =1; i<=12; i++){
            if(i==12 || (this.longitude>houseCusps[i] && this.longitude<houseCusps[i+1])){
                this.houseNumber=i;
                break;
            }
        }
    }

    public int getPlanet_id() {
        return planet_id;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeedLongitude() {
        return speedLongitude;
    }

    public void setSpeedLongitude(double speedLongitude) {
        this.speedLongitude = speedLongitude;
    }

    public double getSpeedLatitude() {
        return speedLatitude;
    }

    public void setSpeedLatitude(double speedLatitude) {
        this.speedLatitude = speedLatitude;
    }

    public double getSpeedDistance() {
        return speedDistance;
    }

    public void setSpeedDistance(double speedDistance) {
        this.speedDistance = speedDistance;
    }

    public int getSignIndex() {
        return signIndex;
    }

    public void setSignIndex(int signIndex) {
        this.signIndex = signIndex;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }
    public void setPlanet_name(String planet_name) {
        this.planet_name = planet_name;
    }
    public String getPlanet_name() {
        return planet_name;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public int getDegreesInSign() {
        return degreesInSign;
    }

    public int getMinutesInSign() {
        return minutesInSign;
    }

    public int getSecondsInSign() {
        return secondsInSign;
    }
}
