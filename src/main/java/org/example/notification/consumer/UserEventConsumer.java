package org.example.notification.consumer;

import org.example.dto.UserEventDto;
import org.example.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final EmailService emailService;

    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service-group")
    public void consumeUserEvent(UserEventDto event) {
        logger.info("Received user event: operation={}, email={}", event.getOperation(), event.getEmail());

        try {
            switch (event.getOperation()) {
                case "CREATE":
                    emailService.sendUserCreatedEmail(event.getEmail());
                    break;
                case "DELETE":
                    emailService.sendUserDeletedEmail(event.getEmail());
                    break;
                default:
                    logger.warn("Unknown operation: {}", event.getOperation());
            }
        } catch (Exception e) {
            logger.error("Error processing user event: operation={}, email={}", 
                event.getOperation(), event.getEmail(), e);
            // В реальном приложении здесь можно добавить retry механизм или dead letter queue
        }
    }
}
