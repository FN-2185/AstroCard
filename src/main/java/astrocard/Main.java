package astrocard;

import astrocard.chart.ChartRequest;
import astrocard.chart.SwissEphemerisAdapter;
import astrocard.telegrambot.natalchartsender.ChartSender;
import java.time.Instant;


public class Main {
    public static void main(String[] args) {
        NatalChart chart = new SwissEphemerisAdapter("./ephemers").calculate(
                new ChartRequest(Instant.parse("2008-10-11T19:55:00Z"), 55.7558, 37.6176));
        System.out.println(new ChartSender().render(chart, "Натальная карта"));
    }
}
