package no.werner.trafficshaping.restserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@Data
@Validated
@ConfigurationProperties(prefix="application")
public class ApplicationConfig {

    @NotNull
    @Valid
    private List<AccountType> accountTypes;

    @NotNull
    @Valid
    private List<AccountConfig> accountConfigs;

    @NotNull
    @Valid
    private CacheConfig cache;

    @NotNull
    @Valid
    private NetworkConfig network;
}
