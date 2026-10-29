package astrocard.chart;

import astrocard.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import swisseph.*;

import java.nio.file.*;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SwissEphemerisAdapter implements Ephemeris {

    private static final Object LIBRARY_LOCK = new Object();
    private final String ephemerisPath;

    public SwissEphemerisAdapter(@Value("${astro.ephemeris-path:./ephemers}") String path) {
        this.ephemerisPath = Path.of(path).toAbsolutePath().normalize().toString();
    }

    @Override
    public NatalChart calculate(ChartRequest request) {
        if (!Files.isDirectory(Path.of(ephemerisPath))) {
            throw new IllegalStateException("Ephemeris directory does not exist: " + ephemerisPath);
        }
        synchronized (LIBRARY_LOCK) {
            SwissEph ephemeris = new SwissEph(ephemerisPath);
            try {
                var utc = request.instant().atOffset(ZoneOffset.UTC);
                double hours = utc.toLocalTime().toNanoOfDay() / 3_600_000_000_000.0;
                double julianDay = new SweDate(utc.getYear(), utc.getMonthValue(), utc.getDayOfMonth(),
                        hours, SweDate.SE_GREG_CAL).getJulDay();
                double[] cusps = new double[13], ascmc = new double[10];
                if (ephemeris.swe_houses(julianDay, 0, request.latitude(), request.longitude(), 'P', cusps, ascmc) < 0) {
                    throw new IllegalArgumentException("Для этих координат система домов Плацидуса недоступна. Проверьте город рождения.");
                }
                Map<Integer, PlanetPosition> positions = new LinkedHashMap<>();
                for (CelestialObject object : CelestialObject.values()) {
                    double[] values = new double[6];
                    StringBuffer diagnostic = new StringBuffer();
                    int flags = ephemeris.swe_calc_ut(julianDay, object.getSwissephId(),
                            SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED, values, diagnostic);
                    if (flags < 0 || (flags & SweConst.SEFLG_SWIEPH) == 0) {
                        throw new IllegalStateException("Ephemeris calculation failed for " + object + ": " + diagnostic);
                    }
                    positions.put(object.getSwissephId(), new PlanetPosition(object.getSwissephId(), values, cusps));
                }
                return new NatalChart(positions, cusps, ascmc);
            } finally {
                ephemeris.swe_close();
            }
        }
    }
}
