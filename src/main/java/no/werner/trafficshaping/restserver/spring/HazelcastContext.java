package no.werner.trafficshaping.restserver.spring;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICacheManager;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;

@Configuration
@RequiredArgsConstructor
public class HazelcastContext {

    private final ApplicationConfig applicationConfig;

    @Bean
    public ProxyManager<String> getCache() {
        Config config = new Config();
        config.setLiteMember(false);
        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        cacheConfig.setName("buckets");
        config.addCacheConfig(cacheConfig);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        ICacheManager cacheManager = hazelcastInstance.getCacheManager();
        Cache<String, GridBucketState> cache = cacheManager.getCache(applicationConfig.getCache().getName());

        return Bucket4j.extension(JCache.class).proxyManagerForCache(cache);
    }
}
