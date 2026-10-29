package astrocard.telegrambot.config;

import astrocard.NatalChart;
import astrocard.chart.ChartRequest;
import astrocard.service.DescriptionKey;
import com.github.benmanes.caffeine.cache.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import java.time.*;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<ChartRequest, NatalChart> chartCache(
            @Value("${astro.cache.charts.maximum-size:2000}") long maximumSize,
            @Value("${astro.cache.charts.ttl:PT6H}") Duration ttl) {
        return Caffeine.newBuilder().maximumSize(maximumSize).expireAfterAccess(ttl).recordStats().build();
    }
    @Bean
    public Cache<DescriptionKey, String> descriptionCache(
            @Value("${astro.cache.descriptions.maximum-size:5000}") long maximumSize,
            @Value("${astro.cache.descriptions.ttl:PT1H}") Duration ttl) {
        return Caffeine.newBuilder().maximumSize(maximumSize).expireAfterWrite(ttl).recordStats().build();
    }
    @Bean public Clock clock() { return Clock.systemUTC(); }
}
