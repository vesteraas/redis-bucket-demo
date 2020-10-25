package no.werner.trafficshaping.restserver;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.processor.EntryProcessor;
import javax.cache.spi.CachingProvider;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

public class CompatibilityTest {

    final Cache<String, Integer> cache;


    public CompatibilityTest() throws URISyntaxException {
        URI redissonConfigUri = getClass().getResource("/redisson-jcache.yml").toURI();
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager(redissonConfigUri, null);
        MutableConfiguration<String, Integer> cacheConfig = new MutableConfiguration<>();
        cacheConfig.setTypes(String.class, Integer.class);
        cacheConfig.setExpiryPolicyFactory(EternalExpiryPolicy.factoryOf());
        this.cache = manager.createCache("rate-limiter", cacheConfig);
    }

    public void test() throws InterruptedException {
        String key = "42";
        int threads = 4;
        int iterations = 1000;
        cache.put(key, 0);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        EntryProcessor<String, Integer, Void> processor = (Serializable & EntryProcessor<String, Integer, Void>) (mutableEntry, objects) -> {
                            int value = mutableEntry.getValue();
                            mutableEntry.setValue(value + 1);
                            return null;
                        };
                        cache.invoke(key, processor);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        int value = cache.get(key);
        if (value == threads * iterations) {
            System.out.println("Implementation which you use is compatible with Bucket4j");
        } else {
            String msg = "Implementation which you use is not compatible with Bucket4j";
            msg += ", " + (threads * iterations - value) + " writes are missed";
            throw new IllegalStateException(msg);
        }
    }

    public static void main(String[] args) throws Exception {
        new CompatibilityTest().test();
    }
}
