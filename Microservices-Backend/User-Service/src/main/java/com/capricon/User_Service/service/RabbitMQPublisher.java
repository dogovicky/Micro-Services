package com.capricon.User_Service.service;

import com.capricon.User_Service.components.RabbitMQProperties;
import com.capricon.User_Service.exception.TechnicalException;
import com.capricon.User_Service.exception.enums.TechnicalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQPublisher {

    private final RabbitMQProperties properties;
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchangeName, String routingKey, Object payload) {
        log.info("RabbitMQ service called...");
        try {
            String exchange = properties.getExchanges().get(exchangeName);
            String key = properties.getRoutingKeys().get(routingKey);

            log.info("Resolved exchange and key: {}, {}", exchange, key);
            rabbitTemplate.convertAndSend(exchange, key, payload);
            log.info("Message processed and sent successfully");
        } catch (Exception ex) {
            log.error("Failed to send rabbitMQ message: {}", ex.getMessage());
            throw new TechnicalException("Failed to send message.", TechnicalErrorCode.SERVICE_UNAVAILABLE);
        }
    }

}
