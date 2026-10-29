package astrocard.chart;

import astrocard.NatalChart;

public interface Ephemeris {
    NatalChart calculate(ChartRequest request);
}
