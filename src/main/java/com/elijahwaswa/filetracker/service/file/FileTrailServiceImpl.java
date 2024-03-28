package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.dto.FileTrailDto;
import com.elijahwaswa.filetracker.dto.SettingDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.FileTrail;
import com.elijahwaswa.filetracker.repository.FileTrailRepository;
import com.elijahwaswa.filetracker.service.setting.SettingService;
import com.elijahwaswa.filetracker.util.DurationType;
import com.elijahwaswa.filetracker.util.Helpers;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class FileTrailServiceImpl implements FileTrailService {
    private ModelMapper modelMapper;
    private FileTrailRepository fileTrailRepository;
    private SettingService settingService;

    @Override
    public FileTrailDto saveFileTrail(FileTrailDto fileTrailDto) {
        FileTrail fileTrail = modelMapper.map(fileTrailDto, FileTrail.class);
        if (fileTrail.getCreatedAt() == null) {
            fileTrail.setCreatedAt(LocalDateTime.now());
        }
        FileTrail savedFileTrail = fileTrailRepository.save(fileTrail);
        return modelMapper.map(savedFileTrail, FileTrailDto.class);
    }

    @Override
    public FileTrailDto updateFileTrail(FileTrailDto fileTrailDto) {
        //fetch file
        FileTrail fileTrail = fileTrailRepository.findById(fileTrailDto.getId());
        if (fileTrail == null)
            throw new ResourceNotFoundException("File trail with id " + fileTrailDto.getId() + " not found");
        //skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(fileTrailDto, fileTrail);
        FileTrail updatedFileTrail = fileTrailRepository.save(fileTrail);
        return modelMapper.map(updatedFileTrail, FileTrailDto.class);
    }

    @Override
    public List<FileTrailDto> fetchFileTrails(int pageNumber, int pageSize, String lrNo) throws ResourceNotFoundException {
        Page<FileTrail> fileTrails = fileTrailRepository.findAllByLrNo(lrNo.toLowerCase(), Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(FileTrailDto.class, fileTrails, "No file trails found!");
    }

    @Override
    public long countByLrNo(String lrNo) {
        return fileTrailRepository.countByLrNo(lrNo);
    }

    @Override
    public FileTrailDto computeDueDate(FileTrailDto fileTrailDto) {
        LocalDateTime dueDate = fileTrailDto.getDueDate();
        SettingDto setting = null;

        try {
            //fetch setting
            List<SettingDto> settings = settingService.fetchSettings(0, 1);
            setting = settings.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dueDate == null) {
            dueDate = setting != null ? computeDueDateUsingSetting(setting) : Helpers.computeDueDateUsingSetting(LocalDateTime.now(), Helpers.REMINDER_DURATION, Helpers.DURATION_TYPE);
        } else {
            //make it end of day
            dueDate = dueDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }


        // ensure the seconds not less than or equal to 0
        LocalDateTime now = LocalDateTime.now();
        long secondsDifference = Helpers.timeDifference(ChronoUnit.SECONDS, now, dueDate);
        if (secondsDifference <= 600) {//not less than 10mins in seconds
            dueDate = Helpers.computeDueDateUsingSetting(LocalDateTime.now(), Helpers.REMINDER_DURATION, Helpers.DURATION_TYPE);
        }

        fileTrailDto.setDueDate(dueDate);
        return fileTrailDto;
    }

    private LocalDateTime computeDueDateUsingSetting(SettingDto setting) {
        long duration = setting.getDuration();
        DurationType durationType = setting.getDurationType();
        return Helpers.computeDueDateUsingSetting(LocalDateTime.now(), duration != 0 ? duration : Helpers.REMINDER_DURATION, durationType != null ? durationType : Helpers.DURATION_TYPE);
    }

}
