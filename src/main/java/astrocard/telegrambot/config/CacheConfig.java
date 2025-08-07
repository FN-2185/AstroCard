package astrocard.telegrambot.config;


import astrocard.entity.Descriptions;
import astrocard.telegrambot.payload.NatalData;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<Long, NatalData> natalDataCache(){
        return Caffeine.newBuilder()
                .expireAfterAccess(7, TimeUnit.HOURS)
                .maximumSize(15)
                .build();
    }
    @Bean
    public Cache<Long, List<Descriptions>> descriptionCache(){
        return Caffeine.newBuilder()
                .expireAfterAccess(7, TimeUnit.HOURS)
                .maximumSize(15)
                .build();
    }
}
