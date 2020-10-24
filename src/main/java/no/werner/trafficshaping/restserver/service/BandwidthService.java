package no.werner.trafficshaping.restserver.service;

import io.github.bucket4j.Bandwidth;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class BandwidthService {

    private Map<String, Bandwidth> bandwidths = new HashMap<>();

    public Bandwidth getBandwidth(String shortNumber) {
        return bandwidths.get(shortNumber);
    }

    public void initializeBandwidths(Map<String, Bandwidth> bandWidths) {
        this.bandwidths = Collections.unmodifiableMap(bandWidths);
    }
}
