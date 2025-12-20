package net.ryzen.paylinksystem.listener;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.service.MessageValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentListener {
    private final PubSubTemplate pubSubTemplate;
    private final MessageValidationService validationService;

    @Value("${paylink.pubsub.topic-payment}")
    private String subscriptionName;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        pubSubTemplate.subscribe(subscriptionName, message -> {
            String payload = message.getPubsubMessage().getData().toStringUtf8();
            log.info("Message received from Pub/Sub: {}", payload);
            try {
                consumePayment(payload);
            } catch (Exception e) {
                log.error("Error processing message from Pub/Sub", e);
            } finally {
                message.ack();
            }
        });
    }

    private void consumePayment(String payload) {
        log.info("--- Consume Data Payment ---");
        validationService.checkValidationMessage(payload, "payment");

    }
}
