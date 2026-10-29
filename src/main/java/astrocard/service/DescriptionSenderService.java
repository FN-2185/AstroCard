package astrocard.service;

import astrocard.NatalChart;
import astrocard.PlanetPosition;
import astrocard.repository.custom.DescriptionsRepository;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DescriptionSenderService {
    private final DescriptionsRepository repository;
    private final Cache<DescriptionKey, String> cache;
    public DescriptionSenderService(DescriptionsRepository repository, Cache<DescriptionKey, String> cache) {
        this.repository = repository; this.cache = cache;
    }

    public String describe(NatalChart chart, int planetId, boolean inHouse) {
        PlanetPosition planet = Objects.requireNonNull(chart.getPlanetPositions().get(planetId), "Planet not calculated");
        DescriptionKey key = new DescriptionKey(planet.getPlanet_name(),
                inHouse ? Integer.toString(planet.getHouseNumber()) : planet.getSignName());
        return cache.get(key, missing -> repository.load(Set.of(missing)).get(missing));
    }
}
