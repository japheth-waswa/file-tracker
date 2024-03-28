package com.elijahwaswa.filetracker.model;

import com.elijahwaswa.filetracker.util.FileTrailOrigin;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "file_trails")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class FileTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime createdAt;
    private LocalDateTime assignedOn;
    private LocalDateTime dispatchedOn;
    private String lrNo;
    private String assignedToIdNumber;
    private String assignedToFullNames;
    private String assignedByIdNumber;
    private String assignedByFullNames;
    private String dispatchedByIdNumber;
    private String dispatchedByFullNames;
    private long timeTakenInSeconds;
    private String department;
    private String dispatchNote;
    private FileTrailOrigin fileTrailOrigin = FileTrailOrigin.NEW_SYSTEM;
    private String createdBy;
    private LocalDateTime dueDate;

    @PrePersist
    @PreUpdate
    public void toLowerCase() {
        if (this.lrNo != null && !this.lrNo.isBlank()) this.lrNo = this.lrNo.toLowerCase();
        if (this.assignedToIdNumber != null && !this.assignedToIdNumber.isBlank())
            this.assignedToIdNumber = this.assignedToIdNumber.toLowerCase();
        if (this.assignedToFullNames != null && !this.assignedToFullNames.isBlank())
            this.assignedToFullNames = this.assignedToFullNames.toLowerCase();
        if (this.assignedByIdNumber != null && !this.assignedByIdNumber.isBlank())
            this.assignedByIdNumber = this.assignedByIdNumber.toLowerCase();
        if (this.assignedByFullNames != null && !this.assignedByFullNames.isBlank())
            this.assignedByFullNames = this.assignedByFullNames.toLowerCase();
        if (this.dispatchedByIdNumber != null && !this.dispatchedByIdNumber.isBlank())
            this.dispatchedByIdNumber = this.dispatchedByIdNumber.toLowerCase();
        if (this.dispatchedByFullNames != null && !this.dispatchedByFullNames.isBlank())
            this.dispatchedByFullNames = this.dispatchedByFullNames.toLowerCase();
        if (this.department != null && !this.department.isBlank()) this.department = this.department.toLowerCase();
        if (this.createdBy != null && !this.createdBy.isBlank()) this.createdBy = this.createdBy.toLowerCase();
    }
}
