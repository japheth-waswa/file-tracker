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
@Table(name = "files")
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
    private FileStatus fileStatus;//todo if FileStatus.LOCKED, then the file can only move to default department & or it cannot move out of default department.
    private FileNature fileNature;
    private String currentDepartment;
    private String currentUserIdNumber;
    private String currentUserFullNames;
    private String createdBy;

    @PrePersist
    @PreUpdate
    public void toLowerCase(){
        if(this.lrNo != null && !this.lrNo.isBlank())this.lrNo = this.lrNo.toLowerCase();
        if(this.irNo != null && !this.irNo.isBlank())this.irNo = this.irNo.toLowerCase();
        if(this.cfNo != null && !this.cfNo.isBlank())this.cfNo = this.cfNo.toLowerCase();
        if(this.currentUserIdNumber != null && !this.currentUserIdNumber.isBlank())this.currentUserIdNumber = this.currentUserIdNumber.toLowerCase();
    }
}
