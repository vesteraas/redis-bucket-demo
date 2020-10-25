package no.werner.trafficshaping.restserver.config;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NetworkConfig {

    @NotNull
    private Boolean multicastEnabled;

    @NotNull
    private Boolean awsEnabled;

    @NotNull
    private Boolean interfacesEnabled;

    private List<String> interfaces;
}
