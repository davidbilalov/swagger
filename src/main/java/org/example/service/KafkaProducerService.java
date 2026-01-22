package org.example.service;

import org.example.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;
    private final String topicName;

    public KafkaProducerService(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = "user-events";
    }

    public void sendUserEvent(String operation, String email) {
        UserEventDto event = new UserEventDto(operation, email);
        
        CompletableFuture<SendResult<String, UserEventDto>> future = 
            kafkaTemplate.send(topicName, email, event);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("Sent message=[{}] with offset=[{}]", 
                    event, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] due to : {}", 
                    event, exception.getMessage());
            }
        });
    }
}
