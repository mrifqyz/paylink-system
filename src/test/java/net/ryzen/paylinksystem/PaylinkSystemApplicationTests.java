package net.ryzen.paylinksystem;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {
        com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration.class
})
class PaylinkSystemApplicationTests {

    @MockBean
    private PubSubTemplate pubSubTemplate;

    @Test
    void contextLoads() {
    }

}
