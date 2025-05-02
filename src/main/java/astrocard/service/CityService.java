package astrocard.service;

import astrocard.entity.Cities;
import astrocard.repository.CityCoordinatesRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityCoordinatesRepository cityRepo;

    public CityService(CityCoordinatesRepository cityRepo) {
        this.cityRepo = cityRepo;
    }
    public Cities getCoordinatesByCity(String city){
        return cityRepo.findByName(city).orElseThrow(()->new RuntimeException("Город не найден: " + city));
    }
    public Cities getCoordinatesByCityAndCountry(String country, String city){
        return this.cityRepo.findCityByCountryAndName(country, city).orElseThrow(()->new RuntimeException("?????"));
    }
}
