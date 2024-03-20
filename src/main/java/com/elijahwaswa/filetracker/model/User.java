package com.elijahwaswa.filetracker.model;

import com.elijahwaswa.filetracker.util.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String idNumber;

    @Column(unique = true)
    private String email;

    private String password;
    private String roles;
    private String rights;
    private String departmentId;
    private String department;
    private AccountStatus accountStatus;
    private String firstName;
    private String middleName;
    private String otherNames;
    private Instant resetPasswordExpiryTime;
    private String resetPasswordToken;
    private String twoFactorSecret;
    private Instant twoFactorSecretExpiryTime;
    @ElementCollection
    private Set<String> usedTotpCodes=new HashSet<>();
}
