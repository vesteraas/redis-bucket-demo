package no.werner.trafficshaping.restserver.controller;

import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.redis.request.RedisRateLimiterFactory;
import lombok.RequiredArgsConstructor;
import no.werner.trafficshaping.restserver.domain.Account;
import no.werner.trafficshaping.restserver.domain.SMS;
import no.werner.trafficshaping.restserver.service.AccountService;
import no.werner.trafficshaping.restserver.service.RateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SMSController {

    private final AccountService accountService;
    private final RateLimiterService rateLimiterService;
    private final RedisRateLimiterFactory backend;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<Void> send(@RequestBody SMS sms) {

        String shortNumber = sms.getShortNumber();

        final Account account = accountService.getAccount(shortNumber);

        final RequestRateLimiter rateLimiter = rateLimiterService.getRateLimiter(account.getType());

        if (rateLimiter.overLimitWhenIncremented(shortNumber)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .build();
        } else {
            return ResponseEntity.noContent()
                    .build();
        }
    }
}
