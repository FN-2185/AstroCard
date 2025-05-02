package astrocard.telegrambot;

import astrocard.NatalChart;
import astrocard.NatalChartService;
import astrocard.PlanetPosition;
import astrocard.entity.Cities;
import astrocard.service.CityService;
import org.springframework.stereotype.Service;
import swisseph.SweDate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class NatalChartFSMService {//переделать ввиде графа
    private CityService cityService;
    private final Map<Long, InputStep> userSteps = new HashMap<>();
    private final Map<Long, NatalData> tempData = new HashMap<>();
    public NatalChartFSMService(CityService cityService){
        this.cityService=cityService;
    }
    public InputStep getStep(long chatId) {
        return userSteps.getOrDefault(chatId, null);
    }

    public void start(long chatId) {
        userSteps.put(chatId, InputStep.COUNTRY);
        tempData.put(chatId, new NatalData());
    }

    public void cancel(long chatId) {
        userSteps.remove(chatId);
        tempData.remove(chatId);
    }

    public String handleInput(long chatId, String input) {
        NatalData data = tempData.get(chatId);
        if (data == null) return "Введите /start сначала.";

        InputStep step = userSteps.get(chatId);

        switch (step) {
            case COUNTRY -> {
                data.setCountry(input);
                userSteps.put(chatId, InputStep.CITY);
                return "Введите город:";
            }
            case CITY -> {
                data.setCity(input);
                userSteps.put(chatId, InputStep.DATE);
                return "Введите дату (дд.мм.гггг):";
            }
            case DATE -> {
                data.setDate(input);
                userSteps.put(chatId, InputStep.TIME);
                return "Введите время (чч:мм):";
            }
            case TIME -> {
                data.setTime(input);
                userSteps.put(chatId, InputStep.DONE);
                return "Спасибо! Вот введённые данные:\n\n" + data;
            }
            case DONE -> {
                String card = String.valueOf(getCard(data));
                return "Natal chart...."+ card;
            }
            default -> {
                return "Неверный шаг. Введите /start для начала.";
            }
        }
    }

    private StringBuilder getCard(NatalData data){/*надо вынести в отдельный класс*/

        StringBuilder card = new StringBuilder();
        card.append("<b>Натальная карта:</b>\n\n");
        String country = translateCountryToEnglish(data.getCountry());
        String city = data.getCity();
        String[] parts = data.getDate().split("\\.");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        parts = data.getTime().split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        Cities cities= this.cityService.getCoordinatesByCityAndCountry(country, city);
        double latitude = cities.getLatitude();
        double longitude = cities.getLongitude();
        SweDate sweDate = new SweDate(year, month,day, hour+(minute/60.0), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), latitude, longitude);
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            PlanetPosition pos = entry.getValue();
            card.append("<b>").append(pos.getPlanet_name()).append("</b>: ")
                    .append("<i>").append(pos.getSignName()).append("</i> ")
                    .append(pos.getDegreesInSign()).append("° ")
                    .append(pos.getMinutesInSign()).append("'\n");
        }
        return card;
    }


    public  String translateCountryToEnglish(String inputRussianCountry) {
        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale("", isoCode);
            String countryInRussian = locale.getDisplayCountry(new Locale("ru"));
            if (countryInRussian.equalsIgnoreCase(inputRussianCountry.trim())) {
                return locale.getDisplayCountry(Locale.ENGLISH);
            }
        }
        return null; // или "Unknown"
    }
}
