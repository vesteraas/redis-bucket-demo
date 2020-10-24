package no.werner.trafficshaping.restserver.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AccountConfig {

    @NotNull
    private String shortNumber;

    @NotNull
    private String type;
}
