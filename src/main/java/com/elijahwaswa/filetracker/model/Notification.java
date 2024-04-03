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
@Table(name = "notifications", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_idNumber", columnList = "idNumber"),
        @Index(name = "idx_message", columnList = "message"),
})
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String idNumber;
    private String message;

    @PrePersist
    @PreUpdate
    public void toLowerCase(){
        if(this.idNumber != null && !this.idNumber.isBlank())this.idNumber = this.idNumber.toLowerCase();
    }
}
