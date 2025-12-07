package net.ryzen.paylinksystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration.class
})
class PaylinkSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
