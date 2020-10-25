package no.werner.trafficshaping.restserver.service;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.redis.request.RedisRateLimiterFactory;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.config.AccountType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisRateLimiterFactory factory;

    private Map<String, RequestRateLimiter> bandwidths = new HashMap<>();

    public RequestRateLimiter getRateLimiter(String shortNumber) {
        return bandwidths.get(shortNumber);
    }

    public void initializeBandwidths(List<AccountType> accountTypes) {
        Map<String, RequestRateLimiter> bandwidthMap = new HashMap<>();

        accountTypes.forEach(
                accountType -> bandwidthMap.put(
                        accountType.getName(),
                        createBandwith(accountType)
                )
        );
        this.bandwidths = Collections.unmodifiableMap(bandwidthMap);
    }

    private RequestRateLimiter createBandwith(AccountType accountType) {
        final RequestLimitRule requestLimitRule = RequestLimitRule.of(accountType.getDuration(), accountType.getMessagesPerDuration());
        return factory.getInstance(Collections.singleton(requestLimitRule));
    }
}
