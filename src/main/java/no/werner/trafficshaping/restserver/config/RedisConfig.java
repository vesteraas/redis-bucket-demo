package no.werner.trafficshaping.restserver.config;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RedisConfig {

    @NotNull
    private String host;

    @NotNull
    private Integer port;

    private String password;

    @NotNull
    private String cacheName;
}
