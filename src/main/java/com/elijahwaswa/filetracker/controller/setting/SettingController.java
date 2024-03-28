package com.elijahwaswa.filetracker.controller.setting;

import com.elijahwaswa.filetracker.controller.user.UserController;
import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.service.setting.SettingService;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.DurationType;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("settings")
@AllArgsConstructor
public class SettingController {
    private SettingService settingService;
    private final Logger LOGGER  = LoggerFactory.getLogger(SettingController.class);

    private String managePage(Model model, SettingDto customSettingDto) {
        return managePage(model, true, customSettingDto);
    }

    private String managePage(Model model, boolean resetSettingDto, SettingDto customSettingDto) {
        if (resetSettingDto && customSettingDto == null) model.addAttribute("settingDto", new SettingDto());
        if (customSettingDto != null) model.addAttribute("settingDto", customSettingDto);

        model.addAttribute("durationTypes", DurationType.values());
        return "setting/manage";
    }

    @GetMapping
    public String settingManage(Model model) {
        SettingDto settingDto = null;
        try{
         List<SettingDto> settings=  settingService.fetchSettings(0,1);
         settingDto = settings.getFirst();
        }catch(Exception e){
            LOGGER.error(e.getMessage());
        }

        return managePage(model, settingDto);
    }

    @PostMapping
    public String modifySetting(Model model, @Valid SettingDto settingDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return managePage(model, false, null);
        }

        if (settingDto.getId() != null) {
            settingService.updateSetting(settingDto);
        } else {
            settingService.saveSetting(settingDto);
        }
        return "redirect:/settings";
    }
    
}
