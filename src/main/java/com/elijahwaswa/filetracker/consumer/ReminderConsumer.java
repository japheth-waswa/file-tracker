package com.elijahwaswa.filetracker.consumer;

import com.elijahwaswa.filetracker.event.ReminderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReminderConsumer {
    private Logger LOGGER  = LoggerFactory.getLogger(ReminderConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.reminder.name}")
    public void consumer(ReminderEvent reminderEvent){

        LOGGER.info(String.format("Event received: %s", reminderEvent));
    }
    //todo check if the file is still in this user's account,if so send a reminder
}
