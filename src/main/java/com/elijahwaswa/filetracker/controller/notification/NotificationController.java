package com.elijahwaswa.filetracker.controller.notification;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.dto.NotificationDto;
import com.elijahwaswa.filetracker.service.notification.NotificationService;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    private NotificationService notificationService;

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("allowDataTable", true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Helpers.setViewModelAttrs(authentication,model);

        return "notification/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> notificationList(Model model,
                                                HttpSession session,
                                                @RequestParam(required = false) String draw,
                                                @RequestParam(required = false) int length,
                                                @RequestParam(required = false) int start,
                                                @RequestParam(required = false, name = "search[value]") String searchValue) {
        List<List<String>> data = null;
        List<NotificationDto> notifications = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idNumber = authentication.getName();

        long total = notificationService.countByIdNumber(idNumber);
        try {
            int pageNumber = start / length;
            AtomicInteger numbering = new AtomicInteger(start);
            notifications = notificationService.fetchNotifications(pageNumber, length, idNumber);

            //transform List<NotificationDto> to List<List<String>> for DataTables
            data = notifications.isEmpty() ? new ArrayList<>() : notifications
                    .stream()
                    .map(notification -> {
                        List<String> row = new ArrayList<>();
//                        numbering.incrementAndGet();
//                        row.add(numbering + "");
                        row.add(notification.getMessage());
                        row.add(Helpers.formatDateTime(notification.getCreatedAt(), Helpers.DATE_TIME_VIEW_FORMAT));
                        return row;
                    })
                    .toList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //return payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", notifications != null ? notifications.size() : 0);
        payload.put("recordsFiltered", total);

        return payload;
    }
}
