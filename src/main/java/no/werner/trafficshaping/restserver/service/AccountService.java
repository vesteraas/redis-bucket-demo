package no.werner.trafficshaping.restserver.service;

import no.werner.trafficshaping.restserver.config.AccountConfig;
import no.werner.trafficshaping.restserver.domain.Account;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private Map<String, Account> accounts;

    public Account getAccount(String shortNumber) {
        return accounts.get(shortNumber);
    }

    public void initializeAccounts(List<AccountConfig> accountConfigs) {
        Map<String, Account> accountMap = new HashMap<>();

        accountConfigs.forEach(
                accountConfig -> accountMap.put(
                        accountConfig.getShortNumber(),
                        createAccount(accountConfig)
                )
        );

        this.accounts = Collections.unmodifiableMap(accountMap);
    }

    private Account createAccount(AccountConfig accountConfig) {
        return Account.builder()
                .shortNumber(accountConfig.getShortNumber())
                .type(accountConfig.getType())
                .build();
    }
}
