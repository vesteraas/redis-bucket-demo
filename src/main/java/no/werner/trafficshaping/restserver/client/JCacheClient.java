package no.werner.trafficshaping.restserver.client;

import io.github.bucket4j.grid.GridBucketState;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import no.werner.trafficshaping.restserver.config.RedisConfig;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;

@Configuration
@RequiredArgsConstructor
public class JCacheClient {

    private final ApplicationConfig applicationConfig;

    @Bean
    public Cache getCache() {
        final RedisConfig redisConfig = applicationConfig.getRedis();

        final Config redissonConfig = new Config();

        redissonConfig.useSingleServer()
                .setAddress(String.format("redis://%s:%d", redisConfig.getHost(), redisConfig.getPort()))
                .setPassword(redisConfig.getPassword());

        CompleteConfiguration<String, GridBucketState> completeConfiguration =
                new MutableConfiguration<String, GridBucketState>()
                        .setTypes(String.class, GridBucketState.class);

        final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        return cacheManager.createCache(
                redisConfig.getCacheName(),
                RedissonConfiguration.fromConfig(redissonConfig, completeConfiguration)
        );
    }
}
