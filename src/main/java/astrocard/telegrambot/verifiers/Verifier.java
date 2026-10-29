package astrocard.telegrambot.verifiers;

import java.time.*;
import java.time.format.*;
import java.util.List;

public final class Verifier {
    private Verifier() {}
    public record BirthMoment(LocalTime localTime, Instant instant) {}

    public static LocalDate isValidDate(String input) {
        return LocalDate.parse(input.trim(), DateTimeFormatter.ofPattern("dd.MM.uuuu")
                .withResolverStyle(ResolverStyle.STRICT));
    }
    public static LocalTime isValidTime(String input) {
        return LocalTime.parse(input.trim(), DateTimeFormatter.ofPattern("HH:mm")
                .withResolverStyle(ResolverStyle.STRICT));
    }

    public static BirthMoment resolveBirthTime(String timezone, LocalDate date, String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length > 2) throw new IllegalArgumentException("Введите время в формате ЧЧ:ММ.");
        LocalTime time = isValidTime(parts[0]);
        LocalDateTime local = LocalDateTime.of(date, time);
        List<ZoneOffset> offsets = ZoneId.of(timezone).getRules().getValidOffsets(local);
        if (offsets.isEmpty()) throw new IllegalArgumentException(
                "Такого местного времени не существовало из-за перевода часов. Проверьте время рождения.");
        if (parts.length == 1 && offsets.size() > 1) throw new IllegalArgumentException(
                "Время неоднозначно из-за перевода часов. Укажите одно из смещений " + offsets + ", например: " + input + " " + offsets.get(0));
        ZoneOffset selected = parts.length == 2 ? ZoneOffset.of(parts[1]) : offsets.get(0);
        if (!offsets.contains(selected)) throw new IllegalArgumentException("Смещение не соответствует часовому поясу города.");
        return new BirthMoment(time, local.toInstant(selected));
    }

    public static ZonedDateTime timeToUtc(String timezone, LocalDate date, LocalTime time) {
        return resolveBirthTime(timezone, date, time.toString()).instant().atZone(ZoneOffset.UTC);
    }
}
