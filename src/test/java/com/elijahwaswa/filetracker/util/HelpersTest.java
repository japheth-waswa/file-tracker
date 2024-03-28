package com.elijahwaswa.filetracker.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class HelpersTest {

    @Test
    void timeDifference() {
        long timeDiff = Helpers.timeDifference(ChronoUnit.MINUTES, LocalDateTime.now().minusMinutes(5),LocalDateTime.now());
        assertEquals(5,timeDiff);
        System.out.println(timeDiff);
    }

    @Test
    void formatDuration() {
        String dateFormated0 = Helpers.formatDuration(43);
        System.out.println(dateFormated0);
        assertEquals("43secs",dateFormated0);

        String dateFormated1 = Helpers.formatDuration(1000);
        System.out.println(dateFormated1);
        assertEquals("16mins 40secs",dateFormated1);

        String dateFormated2 = Helpers.formatDuration(84_930);
        System.out.println(dateFormated2);
        assertEquals("23hrs 35mins 30secs",dateFormated2);

        String dateFormated3 = Helpers.formatDuration(100_000);
        System.out.println(dateFormated3);
        assertEquals("1day 3hrs 46mins 40secs",dateFormated3);

        String dateFormated4 = Helpers.formatDuration(300_000);
        System.out.println(dateFormated4);
        assertEquals("3days 11hrs 20mins",dateFormated4);


        String dateFormated5 = Helpers.formatDuration(20_700_200);
        System.out.println(dateFormated5);
        assertEquals("7months 27days 14hrs 3mins 20secs",dateFormated5);

        String dateFormated6 = Helpers.formatDuration(120_700_200);
        System.out.println(dateFormated6);
        assertEquals("3years 9months 27days 23hrs 50mins",dateFormated6);
    }

}