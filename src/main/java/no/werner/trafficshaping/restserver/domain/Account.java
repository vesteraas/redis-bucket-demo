package no.werner.trafficshaping.restserver.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private String shortNumber;

    private String type;
}
