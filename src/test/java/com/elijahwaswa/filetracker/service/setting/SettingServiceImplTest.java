package com.elijahwaswa.filetracker.service.setting;

import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.util.DurationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettingServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SettingService settingService;
    private SettingDto settingDto1;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"settings"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }

    @BeforeEach
    void setup() {
        truncateH2DB();
        settingDto1 = new SettingDto();
        settingDto1.setDurationType(DurationType.DAYS);
        settingDto1.setDuration(17);
    }

    @Test
    void saveSetting() {
        SettingDto settingDto = settingService.saveSetting(settingDto1);
        System.out.println(settingDto);
        assertEquals(settingDto1.getDurationType(), settingDto.getDurationType());
        assertEquals(settingDto1.getDuration(), settingDto.getDuration());
    }

    @Test
    void updateSetting() {
        SettingDto settingDto = settingService.saveSetting(settingDto1);
        System.out.println(settingDto);
        settingDto.setDuration(200);
        settingDto.setDurationType(DurationType.MINUTES);
        SettingDto updatedSettingDto = settingService.updateSetting(settingDto);
        assertEquals(settingDto.getDurationType(), updatedSettingDto.getDurationType());
        assertEquals(settingDto.getDuration(), updatedSettingDto.getDuration());
    }

    @Test
    void fetchSettings() {
        assertThrows(ResourceNotFoundException.class,()->settingService.fetchSettings(0, 10));
        SettingDto settingDto = settingService.saveSetting(settingDto1);
        List<SettingDto> settings  = settingService.fetchSettings(0, 10);
        assertEquals(1,settings.size());
        assertEquals(settingDto.getDuration(), settings.getFirst().getDuration());
        assertEquals(settingDto.getDurationType(), settings.getFirst().getDurationType());
    }
}