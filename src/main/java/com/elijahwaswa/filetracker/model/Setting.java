package com.elijahwaswa.filetracker.model;

import com.elijahwaswa.filetracker.util.DurationType;
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
@Table(name = "settings", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_durationType", columnList = "durationType"),
        @Index(name = "idx_duration", columnList = "duration"),
})
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private DurationType durationType;
    private long duration;
}
