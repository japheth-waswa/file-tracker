package com.elijahwaswa.filetracker.dto;

import com.elijahwaswa.filetracker.util.FileNature;
import com.elijahwaswa.filetracker.util.FileStatus;
import com.elijahwaswa.filetracker.util.validator.ValidFileNature;
import com.elijahwaswa.filetracker.util.validator.ValidFileStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDto {
    private UUID id;
    private LocalDateTime createdAt;
    private String idNumber;
    private String message;
}
