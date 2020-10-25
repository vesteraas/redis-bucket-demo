package no.werner.trafficshaping.restserver.controller;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.grid.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.werner.trafficshaping.restserver.domain.Account;
import no.werner.trafficshaping.restserver.domain.SMS;
import no.werner.trafficshaping.restserver.service.AccountService;
import no.werner.trafficshaping.restserver.service.BandwidthService;
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
@Slf4j
public class SMSController {

    private final AccountService accountService;
    private final BandwidthService bandwidthService;
    private final ProxyManager<String> buckets;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<Void> send(@RequestBody SMS sms) {

        String shortNumber = sms.getShortNumber();

        final Account account = accountService.getAccount(shortNumber);

        Bucket bucket = buckets.getProxy(shortNumber, getConfigSupplierForAccount(account.getType()));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            log.info("Sending message using short number {}", shortNumber);

            return ResponseEntity.noContent()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .build();
        } else {
            log.warn("Rate exceeded for message using short number {}", shortNumber);

            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, String.valueOf(waitForRefill))
                    .build();
        }
    }

    private Supplier<BucketConfiguration> getConfigSupplierForAccount(String type) {
        return () -> Bucket4j.configurationBuilder()
                .addLimit(bandwidthService.getBandwidth(type))
                .build();
    }
}
