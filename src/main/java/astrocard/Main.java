package astrocard;
import swisseph.SweDate;
import swisseph.SwissEph;
import java.time.*;
import java.time.zone.ZoneRulesProvider;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Main {
    public static void main(String[] args) {
        SweDate sweDate = new SweDate(2001, 10,11, 22+(55.0/60), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), 55.7558, 37.6176);
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            System.out.println(entry.getValue().getPlanet_name()+ " Sign:"+ entry.getValue().getSignName()+" "+entry.getValue().getDegreesInSign()+
                    " "+entry.getValue().getMinutesInSign()+" "+entry.getValue().getMinutesInSign()+
                    " House:"+ entry.getValue().getHouseNumber()+" Speed:"+ entry.getValue().getSpeedLongitude());
        }
        Map<Integer, ZodiacSign> map1 = natalChart.getHousePositionInZodiacSign();
        for (Map.Entry<Integer, ZodiacSign> entry1: map1.entrySet()){
            System.out.println(entry1.getKey()+ " Sign:"+ entry1.getValue());
        }
        List<Aspect> list = natalChart.getAspectList();
        for (Aspect aspect : list){
            System.out.println(aspect.getFirstPlanetId()+" "+aspect.getSecondPlanetId()+" "+ aspect.getAngle());
        }
    }
}