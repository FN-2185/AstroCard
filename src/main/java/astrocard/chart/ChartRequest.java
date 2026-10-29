package astrocard.chart;

import java.time.Instant;
import java.util.Objects;

public record ChartRequest(Instant instant, double latitude, double longitude) {
    public ChartRequest {
        Objects.requireNonNull(instant, "Calculation instant required");
        if (!Double.isFinite(latitude) || Math.abs(latitude) > 90
                || !Double.isFinite(longitude) || Math.abs(longitude) > 180) {
            throw new IllegalArgumentException("Invalid geographic coordinates");
        }
    }
}
