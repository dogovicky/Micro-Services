package com.capricon.User_Service.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQConsumer {

    //@RabbitListener(queues = "${spring.rabbitmq.queues.smsSignup}")
    public void sendValidationCode(String email) {
        log.info("Message consumed and email sent: {}", email);
    }

}
