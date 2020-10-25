package no.werner.trafficshaping.restserver.spring;

import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import no.werner.trafficshaping.restserver.config.RedisConfig;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class RMapBasedRedissonBackendContext {

    private final ApplicationConfig applicationConfig;

    @Bean
    public RMapBasedRedissonBackend getProxyManager() throws SQLException {
        final RedisConfig redisConfig = applicationConfig.getRedis();

        final Config config = new Config();

        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", redisConfig.getHost(), redisConfig.getPort()))
                .setPassword(redisConfig.getPassword());

        final RedissonClient redisson = Redisson.create(config);

        RMap<String, byte[]> buckets = redisson.getMap(redisConfig.getCacheName());

        return new RMapBasedRedissonBackend(buckets, ClientSideConfig.getDefault());
    }
}
