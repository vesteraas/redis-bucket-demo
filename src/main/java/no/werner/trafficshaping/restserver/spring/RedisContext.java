package no.werner.trafficshaping.restserver.spring;

import es.moki.ratelimitj.redis.request.RedisRateLimiterFactory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import no.werner.trafficshaping.restserver.config.RedisConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedisContext {

    private final ApplicationConfig applicationConfig;

    @Bean
    public RedisRateLimiterFactory getRedisRateLimiterFactory() {
        final RedisConfig redisConfig = applicationConfig.getRedis();

        final RedisURI.Builder redisURIBuilder = RedisURI.builder()
                .withHost(redisConfig.getHost())
                .withPort(redisConfig.getPort());

        String password = redisConfig.getPassword();

        if (password != null) {
            redisURIBuilder.withPassword(password);
        }

        final RedisClient redisClient = RedisClient.create(redisURIBuilder.build());

        return new RedisRateLimiterFactory(redisClient);
    }
}
