package com.elijahwaswa.filetracker;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class CustomTester {
    @Test
    void testListToString(){
        System.out.println(List.of("a","b","c"));
        System.out.println(String.join(",",List.of("a","b","c")));
        //page
        int length=10, start=50;
        int page  = start/length;
        System.out.println(page);
        System.out.println(0/10);
    }
}
