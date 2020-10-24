package no.werner.trafficshaping.restserver.lifecycle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import no.werner.trafficshaping.restserver.service.AccountService;
import no.werner.trafficshaping.restserver.service.BandwidthService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartup {

    private final ApplicationConfig applicationConfig;
    private final AccountService accountService;
    private final BandwidthService bandwidthService;

    @PostConstruct
    public void startup() {
        accountService.initializeAccounts(applicationConfig.getAccountConfigs());
        bandwidthService.initializeBandwidths(applicationConfig.getAccountTypes());

        log.info("Application started!");
    }
}
