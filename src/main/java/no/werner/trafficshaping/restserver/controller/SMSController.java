package no.werner.trafficshaping.restserver.controller;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.domain.Account;
import no.werner.trafficshaping.restserver.domain.SMS;
import no.werner.trafficshaping.restserver.service.AccountService;
import no.werner.trafficshaping.restserver.service.BandwidthService;
import no.werner.trafficshaping.restserver.spring.RMapBasedRedissonBackend;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
public class SMSController {

    private final AccountService accountService;
    private final BandwidthService bandwidthService;
    private final RMapBasedRedissonBackend backend;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<Void> send(@RequestBody SMS sms) {

        String shortNumber = sms.getShortNumber();

        final Account account = accountService.getAccount(shortNumber);

        Bucket bucket = backend.builder().buildProxy(shortNumber, getConfigSupplierForAccount(account.getType()));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return ResponseEntity.noContent()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .build();
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, String.valueOf(waitForRefill))
                    .build();
        }
    }

    private Supplier<BucketConfiguration> getConfigSupplierForAccount(String type) {
        return () -> BucketConfiguration.builder()
                .addLimit(bandwidthService.getBandwidth(type))
                .build();
    }
}
