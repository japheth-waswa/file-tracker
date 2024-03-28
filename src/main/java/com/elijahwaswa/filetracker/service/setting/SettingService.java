package com.elijahwaswa.filetracker.service.setting;

import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;

import java.util.List;

public interface SettingService {
    SettingDto saveSetting(SettingDto settingDto);
    SettingDto updateSetting(SettingDto settingDto);
    List<SettingDto> fetchSettings(int pageNumber, int pageSize) throws ResourceNotFoundException;
}
