package no.werner.trafficshaping.restserver.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import no.werner.trafficshaping.restserver.config.AccountType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.bucket4j.Refill.intervally;

@Service
public class BandwidthService {

    private Map<String, Bandwidth> bandwidths;

    public Bandwidth getBandwidth(String shortNumber) {
        return bandwidths.get(shortNumber);
    }

    public void initializeBandwidths(List<AccountType> accountTypes) {
        Map<String, Bandwidth> bandwidthMap = new HashMap<>();

        accountTypes.forEach(
                accountType -> bandwidthMap.put(
                        accountType.getName(),
                        createBandwith(accountType)
                )
        );
        this.bandwidths = Collections.unmodifiableMap(bandwidthMap);
    }

    private Bandwidth createBandwith(AccountType accountType) {
        final Refill refill = intervally(accountType.getMessagesPerDuration(), accountType.getDuration());

        return Bandwidth.classic(accountType.getMessagesPerDuration(), refill);
    }
}
