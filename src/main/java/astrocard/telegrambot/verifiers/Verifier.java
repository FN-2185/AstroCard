package astrocard.telegrambot.verifiers;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.zone.ZoneRules;

public class Verifier {
    public static ZonedDateTime timeToUtc(String timezone, LocalDate date, LocalTime time){
        /*DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate localDate = LocalDate.parse(date, dateFormatter);
        LocalTime localTime = LocalTime.parse(time, timeFormatter);*/
        ZoneId zoneId = ZoneId.of(timezone);

        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        ZoneRules rules = zoneId.getRules();

        boolean isDst = rules.isDaylightSavings(zonedDateTime.toInstant());
        ZoneOffset offset = zonedDateTime.getOffset();

        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
    }
    /*
    public static String dateVerifier(String date){
        String[] parts = date.split("\\.");
    }*/
    public static LocalDate isValidDate(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
                .withResolverStyle(ResolverStyle.STRICT); // строгая проверка
        try {
            return LocalDate.parse(input, formatter);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }
    public static LocalTime isValidTime(String input){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalTime.parse(input, formatter);
        }catch (DateTimeParseException e){
            throw e;
        }
    }
}
