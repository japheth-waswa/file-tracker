package com.elijahwaswa.filetracker.service.notification;

import com.elijahwaswa.filetracker.dto.NotificationDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Notification;
import com.elijahwaswa.filetracker.repository.NotificationRepository;
import com.elijahwaswa.filetracker.util.Helpers;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private ModelMapper modelMapper;
    private NotificationRepository notificationRepository;
    private final Logger lOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    @Override
    public NotificationDto saveNotification(NotificationDto notificationDto) {
        Notification notification = notificationRepository.save(modelMapper.map(notificationDto, Notification.class));
        return modelMapper.map(notification,NotificationDto.class);
    }

    @Override
    public List<NotificationDto> fetchNotifications(int pageNumber, int pageSize, String idNumber) throws ResourceNotFoundException {
        Page<Notification> notifications  = notificationRepository.findAllByIdNumber(idNumber, Helpers.buildPageable(pageNumber,pageSize,List.of(new Sort.Order(Sort.Direction.DESC,"createdAt"))));
        return Helpers.parsePageableRecordsToList(NotificationDto.class,notifications,"No notifications found!");
    }

    @Override
    public long countByIdNumber(String idNumber) {
        return notificationRepository.countByIdNumber(idNumber);
    }
}
