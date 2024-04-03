package com.elijahwaswa.filetracker.model;

import com.elijahwaswa.filetracker.util.FileNature;
import com.elijahwaswa.filetracker.util.FileStatus;
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
@Table(name = "files", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_updatedAt", columnList = "updatedAt"),
        @Index(name = "idx_irNo", columnList = "irNo"),
        @Index(name = "idx_cfNo", columnList = "cfNo"),
        @Index(name = "idx_areaSize", columnList = "areaSize"),
        @Index(name = "idx_fileStatus", columnList = "fileStatus"),
        @Index(name = "idx_fileNature", columnList = "fileNature"),
        @Index(name = "idx_currentDepartment", columnList = "currentDepartment"),
        @Index(name = "idx_currentUserFullNames", columnList = "currentUserFullNames"),
        @Index(name = "idx_createdBy", columnList = "createdBy"),
})
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String lrNo;

    private String irNo;
    private String cfNo;
    private double areaSize;
    private FileStatus fileStatus = FileStatus.TRANSACTABLE;
    private FileNature fileNature = FileNature.LEASEHOLD;
    private String currentDepartment;
    private String currentUserIdNumber;
    private String currentUserFullNames;
    private String createdBy;

    @PrePersist
    @PreUpdate
    public void toLowerCase() {
        if (this.lrNo != null && !this.lrNo.isBlank()) this.lrNo = this.lrNo.toLowerCase();
        if (this.irNo != null && !this.irNo.isBlank()) this.irNo = this.irNo.toLowerCase();
        if (this.cfNo != null && !this.cfNo.isBlank()) this.cfNo = this.cfNo.toLowerCase();
        if (this.currentUserIdNumber != null && !this.currentUserIdNumber.isBlank())
            this.currentUserIdNumber = this.currentUserIdNumber.toLowerCase();
    }
}
