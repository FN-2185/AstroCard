package astrocard.service;

import astrocard.NatalChart;
import astrocard.NatalChartService;
import astrocard.PlanetPosition;
import astrocard.entity.Descriptions;

import astrocard.repository.custom.DescriptionsRepository;
import astrocard.telegrambot.payload.NatalData;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import swisseph.SweDate;

import java.util.*;

@Service
public class DescriptionSenderService {//этот сервис должен обращаться к ChartSender ну или вычитслять карту
    private DescriptionsRepository descriptionsRepository;
    public DescriptionSenderService(DescriptionsRepository descriptionsRepository){
        this.descriptionsRepository=descriptionsRepository;
    }
    public List<Descriptions> getDescriptions(NatalData data) {
        int day = data.getDate().getDayOfMonth();
        int month = data.getDate().getMonthValue();
        int year = data.getDate().getYear();
        int hour = data.getTime().getHour();
        int minute = data.getTime().getMinute();
        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        SweDate sweDate = new SweDate(year, month, day, hour + (minute / 60.0), SweDate.SE_GREG_CAL);
        NatalChartService natalChartService = new NatalChartService();
        NatalChart natalChart = natalChartService.generateNatalChart(sweDate.getJulDay(), latitude, longitude);
        Map<String, String> planet_position_in_signs = new HashMap<>();
        Map<String, String> planet_position_in_house = new HashMap<>();
        Map<Integer, PlanetPosition> map = natalChart.getPlanetPositions();
        for (Map.Entry<Integer, PlanetPosition> entry : map.entrySet()) {
            planet_position_in_signs.put(entry.getValue().getPlanet_name(), entry.getValue().getSignName());
            planet_position_in_house.put(entry.getValue().getPlanet_name(), String.valueOf(entry.getValue().getHouseNumber()));
        }
        return this.descriptionsRepository.getDescriptions(planet_position_in_signs, planet_position_in_house);
    }
}