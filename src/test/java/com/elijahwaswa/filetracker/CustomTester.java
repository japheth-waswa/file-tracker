package com.elijahwaswa.filetracker;

import com.elijahwaswa.filetracker.util.Helpers;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CustomTester {
    @Test
    void testListToString() {
        System.out.println(List.of("a", "b", "c"));
        System.out.println(String.join(",", List.of("a", "b", "c")));
        //page
        int length = 10, start = 50;
        int page = start / length;
        System.out.println(page);
        System.out.println(0 / 10);
    }

    @Test
    void computeTimeTakenInSeconds() {
        LocalDateTime from = LocalDateTime.now().minusMinutes(5);
        LocalDateTime to = LocalDateTime.now();
        // compute difference in minutes of assignedOn and dispatchedOn
        long minutesDifference = ChronoUnit.MINUTES.between(from, to);
        System.out.println(minutesDifference);
        System.out.println(ChronoUnit.MINUTES.between(to, from));
        System.out.println(Helpers.timeDifference(ChronoUnit.SECONDS, from, to));
    }

    @Test
    void testLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.parse("2024-03-29T00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        LocalDateTime dueDateCurrent = dueDate.withHour(now.getHour()).withMinute(now.getMinute()).withSecond(now.getSecond()).withNano(now.getNano());
        LocalDateTime endOfDay = dueDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        System.out.println(dueDate);
        System.out.println(dueDateCurrent);
        System.out.println(endOfDay);
    }
}
