package com.elijahwaswa.filetracker.service.notification;

import com.elijahwaswa.filetracker.dto.NotificationDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NotificationService notificationService;
    private NotificationDto notificationDto1,notificationDto2,notificationDto3;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"notifications"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }

    @BeforeEach
    void setup(){
        truncateH2DB();
        notificationDto1 = new NotificationDto();
        notificationDto1.setIdNumber("1234");
        notificationDto1.setMessage("never mind!");

        notificationDto2 = new NotificationDto();
        notificationDto2.setIdNumber("4321");
        notificationDto2.setMessage("jailed!");

        notificationDto3 = new NotificationDto();
        notificationDto3.setIdNumber("1234");
        notificationDto3.setMessage("all guilty");
    }

    @Test
    void saveNotification() {
        notificationService.saveNotification(notificationDto1);
        NotificationDto savedNotificationDto2  = notificationService.saveNotification(notificationDto2);
        notificationService.saveNotification(notificationDto3);
        assertEquals(notificationDto2.getMessage(),savedNotificationDto2.getMessage());
    }

    @Test
    void fetchNotifications() {
        assertThrows(ResourceNotFoundException.class,()->notificationService.fetchNotifications(0,10,"1234"));
        notificationService.saveNotification(notificationDto1);
        notificationService.saveNotification(notificationDto3);
        notificationService.saveNotification(notificationDto2);
        List<NotificationDto> notifications  = notificationService.fetchNotifications(0,10,"1234");
        assertEquals(2,notifications.size());
        assertEquals(notificationDto3.getMessage(),notifications.getFirst().getMessage());
    }

    @Test
    void countByIdNumber() {
        notificationDto2.setIdNumber("1234");
        notificationService.saveNotification(notificationDto1);
        notificationService.saveNotification(notificationDto3);
        notificationService.saveNotification(notificationDto2);
        long count = notificationService.countByIdNumber("1234");
        assertEquals(3,count);
    }
}