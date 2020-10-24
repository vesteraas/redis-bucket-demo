package no.werner.trafficshaping.restserver.config;

import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
public class RedisConfig {

    @NotNull
    private String host;

    @NotNull
    private Integer port;

    @NotNull
    private String password;

    @NotNull
    private String cacheName;

    @NotNull
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration expiry;
}
