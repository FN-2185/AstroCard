package astrocard.telegrambot.natalchartsender;

import astrocard.NatalChart;
import astrocard.NatalChartService;
import astrocard.PlanetPosition;
import astrocard.telegrambot.payload.NatalData;
import swisseph.SweDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class ChartSender {
    public static String sendChart(NatalData data){
        StringBuilder card = new StringBuilder();
        card.append("<b>Натальная карта:</b>\n\n");
        String country = data.getCountry();
        String city = data.getCity();

        int day = data.getDate().getDayOfMonth();
        int month = data.getDate().getMonthValue();
        int year = data.getDate().getYear();

        int hour = data.getTime().getHour();
        int minute = data.getTime().getMinute();
        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        SweDate sweDate = new SweDate(year, month,day, hour+(minute/60.0), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), latitude, longitude);
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            PlanetPosition pos = entry.getValue();
            card.append("<b>").append(pos.getPlanet_name()).append("</b>: ")
                    .append("<i>").append(pos.getSignName()).append("</i> ")
                    .append(pos.getDegreesInSign()).append("° ")
                    .append(pos.getMinutesInSign())
                    .append("' Дом: ")
                    .append(pos.getHouseNumber())
                    .append("\n");
        }
        return card.toString();
    }
    public static String sendForecast(NatalData data){
        StringBuilder card = new StringBuilder();
        card.append("<b>Прогноз:</b>\n\n");
        String country = data.getCountry();
        String city = data.getCity();

        int day = LocalDate.now().getDayOfMonth();
        int month = LocalDate.EPOCH.getMonthValue();
        int year = LocalDate.EPOCH.getYear();

        int hour = LocalTime.now().getHour();
        int minute = LocalTime.now().getMinute();
        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        SweDate sweDate = new SweDate(year, month,day, hour+(minute/60.0), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), latitude, longitude);
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            PlanetPosition pos = entry.getValue();
            card.append("<b>").append(pos.getPlanet_name()).append("</b>: ")
                    .append("<i>").append(pos.getSignName()).append("</i> ")
                    .append(pos.getDegreesInSign()).append("° ")
                    .append(pos.getMinutesInSign())
                    .append("' Дом: ")
                    .append(pos.getHouseNumber())
                    .append("\n");
        }
        return card.toString();
    }
}
