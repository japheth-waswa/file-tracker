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
@Table(name = "users", indexes = {
        @Index(name = "idx_id", columnList = "id"),
        @Index(name = "idx_createdAt", columnList = "createdAt"),
        @Index(name = "idx_password", columnList = "password"),
        @Index(name = "idx_roles", columnList = "roles"),
        @Index(name = "idx_rights", columnList = "rights"),
        @Index(name = "idx_departmentId", columnList = "departmentId"),
        @Index(name = "idx_department", columnList = "department"),
        @Index(name = "idx_accountStatus", columnList = "accountStatus"),
        @Index(name = "idx_firstName", columnList = "firstName"),
        @Index(name = "idx_middleName", columnList = "middleName"),
        @Index(name = "idx_otherNames", columnList = "otherNames"),
        @Index(name = "idx_resetPasswordExpiryTime", columnList = "resetPasswordExpiryTime"),
        @Index(name = "idx_resetPasswordToken", columnList = "resetPasswordToken"),
        @Index(name = "idx_twoFactorSecret", columnList = "twoFactorSecret"),
        @Index(name = "idx_twoFactorSecretExpiryTime", columnList = "twoFactorSecretExpiryTime"),
})
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
