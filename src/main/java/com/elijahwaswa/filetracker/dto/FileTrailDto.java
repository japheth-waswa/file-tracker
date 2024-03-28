package com.elijahwaswa.filetracker.dto;

import com.elijahwaswa.filetracker.util.FileTrailOrigin;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FileTrailDto {
    private UUID id;
    private LocalDateTime createdAt;
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime assignedOn;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dispatchedOn;

    @Size(min = 1,message = "LR Number is required")
    private String lrNo;

    @Size(min = 1,message = "Select a user to assign the file to!")
    private String assignedToIdNumber;

    private String assignedToFullNames;
    private String assignedByIdNumber;
    private String assignedByFullNames;
    private String dispatchedByIdNumber;
    private String dispatchedByFullNames;
    private long timeTakenInSeconds;
    private String department;

    @Size(min = 1,message = "Provide a reason for dispatching this file.")
    private String dispatchNote;
    private FileTrailOrigin fileTrailOrigin;
    private String createdBy;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
}
