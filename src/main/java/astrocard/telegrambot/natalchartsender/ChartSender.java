package astrocard.telegrambot.natalchartsender;

import astrocard.NatalChart;
import astrocard.PlanetPosition;
import org.springframework.stereotype.Component;


@Component
public class ChartSender {
    public String render(NatalChart chart, String title) {
        StringBuilder text = new StringBuilder("<b>").append(title).append("</b>\n\n");
        for (PlanetPosition position : chart.getPlanetPositions().values()) {
            text.append("<b>").append(position.getPlanet_name()).append("</b>: <i>")
                    .append(position.getSignName()).append("</i> ").append(position.getDegreesInSign())
                    .append("° ").append(position.getMinutesInSign()).append("′ Дом: ")
                    .append(position.getHouseNumber()).append('\n');
        }
        return text.toString();
    }
}
