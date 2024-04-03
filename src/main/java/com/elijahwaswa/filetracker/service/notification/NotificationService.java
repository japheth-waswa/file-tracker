package com.elijahwaswa.filetracker.service.notification;

import com.elijahwaswa.filetracker.dto.NotificationDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;

import java.util.List;

public interface NotificationService {
    NotificationDto saveNotification(NotificationDto notificationDto);
    List<NotificationDto> fetchNotifications(int pageNumber, int pageSize, String idNumber)throws ResourceNotFoundException;
    long countByIdNumber(String idNumber);
}
