package astrocard.service;

import astrocard.entity.Cities;
import astrocard.entity.CountryCode;
import astrocard.entity.UserData;
import astrocard.exception.InvalidCountryCityException;
import astrocard.repository.CityCoordinatesRepository;
import astrocard.repository.CountryCodeRepository;
import astrocard.repository.UserDataRepository;
import astrocard.telegrambot.eventstate.BotState;
import astrocard.telegrambot.payload.NatalData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class CityService {
    private final CityCoordinatesRepository cityRepo;
    private final CountryCodeRepository countryCodeRepository;
    private final UserDataRepository userDataRepository;

    public CityService(CityCoordinatesRepository cityRepo, CountryCodeRepository countryCodeRepository, UserDataRepository userDataRepository) {
        this.cityRepo = cityRepo;
        this.countryCodeRepository=countryCodeRepository;
        this.userDataRepository=userDataRepository;
    }
    public Cities getCoordinatesByCity(String city){
        return cityRepo.findByName(city).orElseThrow(()->new RuntimeException("Город не найден: " + city));
    }
    public Cities getCoordinatesByCityAndCountry(String country, String city) throws InvalidCountryCityException{
        return this.cityRepo.findCityByCountryAndName(country, city).orElseThrow(()->new InvalidCountryCityException("?????"));
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

    public void saveUserData(NatalData natalData, BotState state){
        UserData userData = new UserData(natalData.getChatID(), natalData.getCountry(), natalData.getCountryCode(),
                natalData.getCity(), natalData.getDate(), natalData.getTime(), natalData.getLatitude(), natalData.getLongitude(), state);
        this.userDataRepository.save(userData);
    }
    public Optional<NatalData> userExists(long chatId){
        Optional <UserData> userData= this.userDataRepository.findUserDataByChatID(chatId);
        if (userData.isEmpty()) return Optional.empty();
        NatalData natalData = new NatalData(userData.get().getChatID(), userData.get().getCountry(), userData.get().getCountry_code(),
                userData.get().getCity(), userData.get().getBirthDate(), userData.get().getBirthTime(), userData.get().getLatitude(), userData.get().getLongitude(), userData.get().getFsm());
        return Optional.of(natalData);
    }
    public void saveUserCountry(long chatId,  String text, BotState state){
        NatalData data = userExists(chatId).get();
        data.setCountry(text);
        saveUserData(data, state);
    }
    public void saveUserCity(long chatId,  String text, BotState state){
        NatalData data = userExists(chatId).get();
        data.setCity(text);
        saveUserData(data, state);
    }
    public void saveUserDate(long chatId, LocalDate date, BotState state){
        NatalData data = userExists(chatId).get();
        data.setDate(date);
        saveUserData(data, state);
    }
    public void saveUserTime(long chatId, LocalTime time, BotState state){
        NatalData data = userExists(chatId).get();
        data.setTime(time);
        saveUserData(data, state);
    }

}
