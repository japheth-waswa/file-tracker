package com.elijahwaswa.filetracker.publisher;

import com.elijahwaswa.filetracker.event.ReminderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReminderProducer {
    private Logger LOGGER = LoggerFactory.getLogger(ReminderProducer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.binding.routing.key}")
    private String reminderRoutingKey;

    private RabbitTemplate rabbitTemplate;

    public ReminderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(ReminderEvent remainderEvent, long delayInMilliseconds) {
        LOGGER.info(String.format("Sending message to RabbitMQ mills(%s): %s", delayInMilliseconds, remainderEvent));

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setDelayLong(delayInMilliseconds);//set delay in milliseconds
            return message;
        };
        rabbitTemplate.convertAndSend(exchange, reminderRoutingKey, remainderEvent, messagePostProcessor);
    }
}
