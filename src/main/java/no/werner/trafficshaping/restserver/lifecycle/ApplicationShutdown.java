package no.werner.trafficshaping.restserver.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class ApplicationShutdown {

    @PreDestroy
    public void destroy() {
        log.info("Application shutting down...");
    }
}
