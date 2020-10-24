package no.werner.trafficshaping.restserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.cache.Cache;

@SpringBootApplication
public class RestServerApplication {

	@Autowired
	public Cache cache;

	public static void main(String[] args) {
		SpringApplication.run(RestServerApplication.class, args);
	}

}
