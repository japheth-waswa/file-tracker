package com.elijahwaswa.filetracker.consumer;

import com.elijahwaswa.filetracker.dto.NotificationDto;
import com.elijahwaswa.filetracker.event.ReminderEvent;
import com.elijahwaswa.filetracker.service.file.FileTrailService;
import com.elijahwaswa.filetracker.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class ReminderConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(ReminderConsumer.class);
    private FileTrailService fileTrailService;
    private NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.reminder.name}")
    public void consumer(ReminderEvent reminderEvent) {
        LOGGER.info(String.format("Event received: %s", reminderEvent));
        processReminderEvent(reminderEvent);
    }

    private void processReminderEvent(ReminderEvent reminderEvent) {
        boolean shouldRemind = fileTrailService.shouldRemind(reminderEvent);
        if (!shouldRemind) return;

        // should remind thus create record in notification service
        String message = String.format("The file (%s) is long overdue. Please dispatch this file.", reminderEvent.getLrNo());
        NotificationDto notification = new NotificationDto();
        notification.setIdNumber(reminderEvent.getAssignedToIdNumber());
        notification.setMessage(message);
        notificationService.saveNotification(notification);
    }
}
