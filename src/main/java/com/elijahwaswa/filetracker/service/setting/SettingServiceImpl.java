package com.elijahwaswa.filetracker.service.setting;

import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Setting;
import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.SettingRepository;
import com.elijahwaswa.filetracker.util.Helpers;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SettingServiceImpl implements SettingService {
    private ModelMapper modelMapper;
    private SettingRepository settingRepository;

    @Override
    public SettingDto saveSetting(SettingDto settingDto) {
        Setting savedSetting  = settingRepository.save(modelMapper.map(settingDto, Setting.class));
        return modelMapper.map(savedSetting,SettingDto.class);
    }

    @Override
    public SettingDto updateSetting(SettingDto settingDto) {
        Setting setting = settingRepository.findById(settingDto.getId());
        if (setting == null) throw new ResourceNotFoundException("Setting with id " + settingDto.getId() + " not found");

        //configure ModelMapper to skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        //Map non-null fields
        modelMapper.map(settingDto, setting);

        //update settings
        Setting updatedSetting = settingRepository.save(setting);
        return modelMapper.map(updatedSetting, SettingDto.class);
    }

    @Override
    public List<SettingDto> fetchSettings(int pageNumber, int pageSize) throws ResourceNotFoundException {
        Page<Setting> settings = settingRepository.findAll(Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(SettingDto.class, settings, "No settings found");
    }
}
