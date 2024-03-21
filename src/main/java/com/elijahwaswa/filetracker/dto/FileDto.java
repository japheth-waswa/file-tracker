package com.elijahwaswa.filetracker.dto;

import com.elijahwaswa.filetracker.util.FileNature;
import com.elijahwaswa.filetracker.util.FileStatus;
import com.elijahwaswa.filetracker.util.validator.ValidAccountStatus;
import com.elijahwaswa.filetracker.util.validator.ValidFileNature;
import com.elijahwaswa.filetracker.util.validator.ValidFileStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileDto {
    private UUID id;

    @Size(min = 1,message = "LR Number is required")
    private String lrNo;

    private String irNo;
    private String cfNo;
    private double areaSize;

    @ValidFileStatus
    private FileStatus fileStatus;

    @ValidFileNature
    private FileNature fileNature;

    private String currentDepartment;
    private String currentUserIdNumber;
    private String currentUserFullNames;
    private String createdBy;
}
