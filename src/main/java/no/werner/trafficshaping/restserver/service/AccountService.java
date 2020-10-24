package no.werner.trafficshaping.restserver.service;

import no.werner.trafficshaping.restserver.domain.Account;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    private Map<String, Account> accounts = new HashMap<>();

    public Account getAccount(String shortNumber) {
        return accounts.get(shortNumber);
    }

    public void initializeAccounts(Map<String, Account> accounts) {
        this.accounts = Collections.unmodifiableMap(accounts);
    }
}
