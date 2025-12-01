package com.example.demo;

import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AMQProducerService {

    private static final Logger log = LoggerFactory.getLogger(AMQProducerService.class);
    private final JmsTemplate jmsTemplate;

    public AMQProducerService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessages(String queueName, int count) {
        log.info("Producing {} messages to queue '{}'", count, queueName);

        for (int i = 1; i <= count; i++) {
            final int index = i;
            String correlationId = UUID.randomUUID().toString();
            String body = "Message #" + i + " correlationId=" + correlationId;

            boolean sent = false;
            int attempts = 0;
            int maxAttempts = 5;
            long delayMs = 5000;
            log.info("Sending " + body);
            while (!sent && attempts < maxAttempts) {
                try {
                    jmsTemplate.send(queueName, session -> {
                        TextMessage msg = session.createTextMessage(body);
                        msg.setJMSCorrelationID(correlationId);
                        msg.setStringProperty("E2EID", "E2E-" + index);
                        msg.setLongProperty("timestampSubmitter", System.currentTimeMillis());
                        return msg;
                    });
                    sent = true;
                    log.info("✅ Sent message {} (attempt {}) queue {}", index, attempts + 1, queueName);
                } catch (JmsException e) {
                    attempts++;
                    log.warn("⚠️ JMS send failed for message {} (attempt {}/{}): {}", i, attempts, maxAttempts, e.getMessage());
                    if (attempts >= maxAttempts) {
                        log.error("❌ Giving up on message {}", i);
                    } else {
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        }

        log.info("Finished producing {} messages to '{}'", count, queueName);
    }
}
