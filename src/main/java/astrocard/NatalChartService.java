package astrocard;

import swisseph.SweConst;
import swisseph.SwissEph;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NatalChartService {
    public static class HouseInfo {
        private double[] cusps;
        private double[] ascmc;

        public HouseInfo(double[] cusps, double[] ascmc) {
            this.cusps = cusps;
            this.ascmc = ascmc;
        }

        public double[] getCusps() {
            return cusps;
        }

        public double[] getAscmc() {
            return ascmc;
        }
    }

    public static double [] calculatePlanetData(double juldate, int planet_number) {
        SwissEph sweph = new SwissEph();
        sweph.swe_set_ephe_path("C:\\Users\\Niko\\Desktop\\Projects\\AstroCard\\ephemers");
        double [] xx = new double[6];
        sweph.swe_calc_ut(juldate, planet_number, SweConst.SEFLG_SPEED, xx, new StringBuffer()); //SEFLG_SWIEPH
        return xx;
    }
    public static HouseInfo calculateHouses(double tjd_ut, double geolat, double geolon){
        double[] cusp = new double [13];
        double[] ascmc = new double [10];
        SwissEph sweph = new SwissEph();
        sweph.swe_houses(tjd_ut, SweConst.SEFLG_SWIEPH, geolat, geolon, (int)'P', cusp, ascmc);
        return new HouseInfo(cusp, ascmc);
    }
    public NatalChart generateNatalChart(double juldate, double geolat, double geolon){
        NatalChart natalChart = new NatalChart();
        HouseInfo houseInfo = NatalChartService.calculateHouses(juldate, geolat, geolon);
        natalChart.setHouseCusps(houseInfo.getCusps());
        natalChart.setAsmc(houseInfo.getAscmc());
        double [] houseLongBegin = natalChart.getHouseCusps();
        for(CelestialObject obj : CelestialObject.values()){
            int planetIndex = obj.getSwissephId();
            natalChart.getPlanetPositions().put(planetIndex, new PlanetPosition(planetIndex, NatalChartService.calculatePlanetData(juldate, planetIndex), houseLongBegin));
        }
        natalChart.setHousePositionInZodiacSign();
        natalChart.setAspectList();
        return natalChart;
    }
}
