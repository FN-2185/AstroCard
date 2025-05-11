package astrocard.service;

import astrocard.entity.Cities;
import astrocard.entity.CountryCode;
import astrocard.exception.InvalidCountryCityException;
import astrocard.repository.CityCoordinatesRepository;
import astrocard.repository.CountryCodeRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@Service
public class CityService {
    private final CityCoordinatesRepository cityRepo;
    private final CountryCodeRepository countryCodeRepository;

    public CityService(CityCoordinatesRepository cityRepo, CountryCodeRepository countryCodeRepository) {
        this.cityRepo = cityRepo;
        this.countryCodeRepository=countryCodeRepository;
    }
    public Cities getCoordinatesByCity(String city){
        return cityRepo.findByName(city).orElseThrow(()->new RuntimeException("Город не найден: " + city));
    }
    public Cities getCoordinatesByCityAndCountry(String country, String city){
        return this.cityRepo.findCityByCountryAndName(country, city).orElseThrow(()->new RuntimeException("?????"));
    }
    public ArrayList<String> validateCountry(String country){
        for (String isoCode : Locale.getISOCountries()) {
            Locale locale = new Locale("", isoCode);
            String countryInRussian = locale.getDisplayCountry(new Locale("ru"));
            if (countryInRussian.equalsIgnoreCase(country.trim())) {
                country=locale.getDisplayCountry(Locale.ENGLISH);
            }
        }
        CountryCode result = countryCodeRepository.findCountryCodeByCountry(country).orElseThrow(()->  new InvalidCountryCityException("Страна не найдена: "));
        ArrayList<String> list = new ArrayList<>();
        list.add(0, result.getCountry());
        list.add(1, result.getIso());
        return list;
    }

}
