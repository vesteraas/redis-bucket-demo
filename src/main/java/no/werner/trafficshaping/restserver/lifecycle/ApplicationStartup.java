package no.werner.trafficshaping.restserver.lifecycle;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.werner.trafficshaping.restserver.config.ApplicationConfig;
import no.werner.trafficshaping.restserver.domain.Account;
import no.werner.trafficshaping.restserver.service.AccountService;
import no.werner.trafficshaping.restserver.service.BandwidthService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.bucket4j.Bandwidth.classic;
import static io.github.bucket4j.Refill.intervally;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartup {

    private final ApplicationConfig applicationConfig;
    private final AccountService accountService;
    private final BandwidthService bandwidthService;
    private final Cache cache;

    private ProxyManager proxyManager;

    @Bean
    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    @PostConstruct
    public void startup() {
        Map<String, Account> accounts = new HashMap<>();

        applicationConfig.getAccountConfigs().stream()
                .forEach(accountConfig -> {
                    final Account account = Account.builder()
                            .shortNumber(accountConfig.getShortNumber())
                            .type(accountConfig.getType())
                            .build();

                    accounts.put(account.getShortNumber(), account);
                });

        accountService.initializeAccounts(accounts);

        Map<String, Bandwidth> bandwidths = new HashMap<>();

        applicationConfig.getAccountTypes().forEach(accountType -> {
            final Refill refill = intervally(
                    accountType.getMessagesPerDuration(),
                    accountType.getDuration()
            );

            bandwidths.put(accountType.getName(), classic(accountType.getMessagesPerDuration(), refill));
        });

        bandwidthService.initializeBandwidths(bandwidths);

        proxyManager = Bucket4j.extension(JCache.class).proxyManagerForCache(cache);

        log.info("Application started!");
    }
}
