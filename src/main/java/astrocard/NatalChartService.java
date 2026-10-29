package astrocard;

import astrocard.chart.ChartRequest;
import astrocard.chart.Ephemeris;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

@Service
public class NatalChartService {
    private final Ephemeris ephemeris;
    private final Cache<ChartRequest, NatalChart> cache;

    public NatalChartService(Ephemeris ephemeris, Cache<ChartRequest, NatalChart> cache) {
        this.ephemeris = ephemeris;
        this.cache = cache;
    }

    public NatalChart getChart(ChartRequest request) {
        return cache.get(request, ephemeris::calculate);
    }
}
