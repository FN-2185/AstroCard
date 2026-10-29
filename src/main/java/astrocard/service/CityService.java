package astrocard.service;

import astrocard.entity.*;
import astrocard.exception.InvalidCountryCityException;
import astrocard.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CityService {
    private final CityCoordinatesRepository cityRepo;
    private final CountryCodeRepository countryRepo;
    public CityService(CityCoordinatesRepository cityRepo, CountryCodeRepository countryRepo) {
        this.cityRepo = cityRepo; this.countryRepo = countryRepo;
    }

    public CountryCode validateCountry(String input) {
        String country = input.trim();
        for (String code : Locale.getISOCountries()) {
            Locale locale = new Locale("", code);
            if (locale.getDisplayCountry(Locale.forLanguageTag("ru")).equalsIgnoreCase(country)) {
                country = locale.getDisplayCountry(Locale.ENGLISH);
                break;
            }
        }
        return countryRepo.findCountryCodeByCountry(country)
                .orElseThrow(() -> new InvalidCountryCityException("Страна не найдена. Проверьте название."));
    }

    public Cities getCoordinatesByCityAndCountry(String country, String city) {
        List<Cities> matches = cityRepo.findCandidates(country, city.trim());
        if (matches.isEmpty()) throw new InvalidCountryCityException("Город не найден. Введите полное название.");
        if (matches.size() > 1) throw new InvalidCountryCityException(
                "Найдено несколько городов. Уточните название или введите GeoNames ID города.");
        return matches.get(0);
    }
}
