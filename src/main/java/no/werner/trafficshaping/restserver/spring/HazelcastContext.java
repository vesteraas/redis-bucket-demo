package no.werner.trafficshaping.restserver.spring;

import com.hazelcast.config.*;
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
        String cacheName = applicationConfig.getCache().getName();

        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        cacheConfig.setName(cacheName);

        Config config = new Config();
        config.setLiteMember(false);
        config.addCacheConfig(cacheConfig);
        config.setNetworkConfig(getNetworkConfig(applicationConfig.getNetwork()));

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        ICacheManager cacheManager = hazelcastInstance.getCacheManager();
        Cache<String, GridBucketState> cache = cacheManager.getCache(cacheName);

        return Bucket4j.extension(JCache.class).proxyManagerForCache(cache);
    }

    private NetworkConfig getNetworkConfig(no.werner.trafficshaping.restserver.config.NetworkConfig networkConfig) {
        JoinConfig joinConfig = new JoinConfig();

        MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setEnabled(networkConfig.getMulticastEnabled());
        joinConfig.setMulticastConfig(multicastConfig);

        AwsConfig awsConfig = new AwsConfig();
        awsConfig.setEnabled(networkConfig.getAwsEnabled());
        joinConfig.setAwsConfig(awsConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig();
        interfacesConfig.setEnabled(networkConfig.getInterfacesEnabled());

        if (networkConfig.getInterfaces() != null) {
            for (String networkInterface : networkConfig.getInterfaces()) {
                interfacesConfig.addInterface(networkInterface);
            }
        }

        NetworkConfig result = new NetworkConfig();
        result.setJoin(joinConfig);
        result.setInterfaces(interfacesConfig);

        return result;
    }
}
