package no.werner.trafficshaping.restserver.config;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Data
public class AccountType {

    @NotNull
    private String name;

    @NotNull
    private Duration duration;

    @NotNull
    private Integer messagesPerDuration;
}
