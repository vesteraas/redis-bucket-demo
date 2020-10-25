package no.werner.trafficshaping.restserver.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CacheConfig {

    @NotNull
    private String name;
}
