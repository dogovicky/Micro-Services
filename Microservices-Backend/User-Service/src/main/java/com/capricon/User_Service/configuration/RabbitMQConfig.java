package com.capricon.User_Service.configuration;

import com.capricon.User_Service.components.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUri(host);
        connectionFactory.setPort(Integer.parseInt(port));
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // Exchanges, queues and routing keys configurations
    @Bean
    public Map<String, DirectExchange> exchanges() {
         return properties.getExchanges().entrySet().stream()
                 .collect(Collectors.toMap(Map.Entry::getKey,
                         entry -> new DirectExchange(entry.getValue())));
    }

    @Bean
    public Map<String, Queue> queues() {
        return properties.getQueues().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new Queue(entry.getValue())));
    }

    @Bean
    public Declarables bindings() {
        return new Declarables(
                properties.getRoutingKeys().entrySet().stream()
                        .map(entry -> {
                            String routingKeyName = entry.getKey(); // The key name "emailSignup"
                            String routingKey = entry.getValue(); // The actual key value "user.signup.email"
                            String queueName = properties.getQueues().get(routingKeyName.split("\\.")[0]);

                            String exchangeName = properties.getExchanges().entrySet()
                                    .stream()
                                    .filter(e -> routingKeyName.startsWith(e.getKey()))
                                    .map(Map.Entry::getValue)
                                    .findFirst()
                                    .orElseThrow(() ->
                                            new IllegalStateException("No matching exchange for routing key: "
                                                    + routingKeyName));

                            return BindingBuilder.bind(new Queue(queueName, true))
                                    .to(new DirectExchange(exchangeName))
                                    .with(routingKey);
                        }).toList()
        );
    }


}
