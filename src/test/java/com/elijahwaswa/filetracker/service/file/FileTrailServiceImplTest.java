package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileTrailDto;
import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.service.setting.SettingService;
import com.elijahwaswa.filetracker.util.DurationType;
import com.elijahwaswa.filetracker.util.Helpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileTrailServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FileTrailService fileTrailService;
    @Autowired
    private SettingService settingService;
    private FileTrailDto fileTrailDto1,fileTrailDto2,fileTrailDto3;
    private SettingDto settingDto;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"file_trails","settings"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }

    @BeforeEach
    void setUp() {
        truncateH2DB();
        settingDto = new SettingDto();
        settingDto.setDurationType(DurationType.HOURS);
        settingDto.setDuration(3);

        fileTrailDto1 = new FileTrailDto();
        fileTrailDto1.setLrNo("LR/2021/1");
        fileTrailDto1.setAssignedOn(LocalDateTime.now());
        fileTrailDto1.setAssignedByFullNames("john doe");

        fileTrailDto2 = new FileTrailDto();
        fileTrailDto2.setLrNo("LR/5051");
        fileTrailDto2.setDispatchNote("Interacted with me!");
        fileTrailDto2.setAssignedByIdNumber("12345678");

        fileTrailDto3 = new FileTrailDto();
        fileTrailDto3.setLrNo("LR/2021/1");
        fileTrailDto3.setDispatchedOn(LocalDateTime.now());
        fileTrailDto3.setDispatchedByFullNames("jane doe");
    }

    @Test
    void saveFileTrail() {
        FileTrailDto savedFileTrailDto1=fileTrailService.saveFileTrail(fileTrailDto1);
        FileTrailDto savedFileTrailDto2=fileTrailService.saveFileTrail(fileTrailDto2);
        FileTrailDto savedFileTrailDto3=fileTrailService.saveFileTrail(fileTrailDto3);

        assertNotNull(savedFileTrailDto1);
        assertNotNull(savedFileTrailDto2);
        assertNotNull(savedFileTrailDto3);
    }

    @Test
    void fetchFileTrails() {
        FileTrailDto savedFileTrailDto1=fileTrailService.saveFileTrail(fileTrailDto1);
        FileTrailDto savedFileTrailDto2=fileTrailService.saveFileTrail(fileTrailDto2);
        FileTrailDto savedFileTrailDto3=fileTrailService.saveFileTrail(fileTrailDto3);
        List<FileTrailDto> fileTrails  = fileTrailService.fetchFileTrails(0,10,fileTrailDto1.getLrNo());
        assertEquals(2,fileTrails.size());
        assertEquals(fileTrailDto3.getDispatchedOn(),fileTrails.getFirst().getDispatchedOn());
        assertEquals(fileTrailDto1.getAssignedByFullNames(),fileTrails.get(1).getAssignedByFullNames());
    }

    @Test
    void countByLrNo() {
        FileTrailDto savedFileTrailDto1=fileTrailService.saveFileTrail(fileTrailDto1);
        FileTrailDto savedFileTrailDto2=fileTrailService.saveFileTrail(fileTrailDto2);
        FileTrailDto savedFileTrailDto3=fileTrailService.saveFileTrail(fileTrailDto3);
        assertEquals(2,fileTrailService.countByLrNo(fileTrailDto3.getLrNo()));
    }

    @Test
    void updateFileTrail() {
        FileTrailDto savedFileTrailDto1=fileTrailService.saveFileTrail(fileTrailDto1);
        FileTrailDto savedFileTrailDto2=fileTrailService.saveFileTrail(fileTrailDto2);
        FileTrailDto savedFileTrailDto3=fileTrailService.saveFileTrail(fileTrailDto3);
        savedFileTrailDto2.setDispatchedByFullNames("rebecca");
        fileTrailService.updateFileTrail(savedFileTrailDto2);
        List<FileTrailDto> fileTrails  = fileTrailService.fetchFileTrails(0,1,fileTrailDto2.getLrNo());
        assertEquals(1,fileTrails.size());
        assertEquals("rebecca",fileTrails.getFirst().getDispatchedByFullNames());
    }

    @Test
    void computeDueDate() {
        fileTrailDto1.setDueDate(LocalDateTime.now().plusDays(2));
        System.out.println(fileTrailDto1);
        FileTrailDto computedDueDate = fileTrailService.computeDueDate(fileTrailDto1);
        System.out.println(computedDueDate);
        assertEquals(LocalDateTime.now().plusDays(2).withHour(23).withMinute(59).withSecond(59).withNano(999999999),computedDueDate.getDueDate());

        fileTrailDto1.setDueDate(null);
        FileTrailDto computedDueDate2 = fileTrailService.computeDueDate(fileTrailDto1);
        System.out.println(computedDueDate2);
        assertEquals(LocalDateTime.now().plusDays(Helpers.REMINDER_DURATION).withHour(23).withMinute(59).withSecond(59).withNano(999999999),computedDueDate2.getDueDate());

        settingService.saveSetting(settingDto);
        fileTrailDto1.setDueDate(null);
        FileTrailDto computedDueDate3 = fileTrailService.computeDueDate(fileTrailDto1);
        System.out.println(computedDueDate3);
    }
}