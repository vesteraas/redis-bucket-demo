package no.werner.trafficshaping.restserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.werner.trafficshaping.restserver.domain.SMS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class IntegrationTest {

    public static final String X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis")
            .withExposedPorts(6379)
            .withCommand("--requirepass ok");

    @DynamicPropertySource
    static void registerRedisPortProperty(DynamicPropertyRegistry registry) {
        registry.add("application.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void sendShouldWorkThenFail() throws Exception {
        SMS sms = SMS.builder().shortNumber("20000").from("555-1212").to("555-2424").content("Testing").build();

        String json = new ObjectMapper().writeValueAsString(sms);

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(204))
                .andExpect(header().string(X_RATE_LIMIT_REMAINING, "0"));

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(429))
                .andExpect(header().exists(HttpHeaders.RETRY_AFTER));
    }

    @Test
    void sendShouldWorkBothTimes() throws Exception {
        SMS sms = SMS.builder().shortNumber("21111").from("555-1212").to("555-2424").content("Testing").build();

        String json = new ObjectMapper().writeValueAsString(sms);

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(204))
                .andExpect(header().string(X_RATE_LIMIT_REMAINING, "1"));

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(204))
                .andExpect(header().string(X_RATE_LIMIT_REMAINING, "0"));
    }

    @Test
    void sendShouldFailTheThirdTime() throws Exception {
        SMS sms = SMS.builder().shortNumber("22222").from("555-1212").to("555-2424").content("Testing").build();

        String json = new ObjectMapper().writeValueAsString(sms);

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(204))
                .andExpect(header().string(X_RATE_LIMIT_REMAINING, "1"));

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(204))
                .andExpect(header().string(X_RATE_LIMIT_REMAINING, "0"));

        mvc.perform(MockMvcRequestBuilders.post("/send")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is(429))
                .andExpect(header().exists(HttpHeaders.RETRY_AFTER));
    }
}