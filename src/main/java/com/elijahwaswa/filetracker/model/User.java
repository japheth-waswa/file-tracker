package com.elijahwaswa.filetracker.model;

import com.elijahwaswa.filetracker.util.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String idNumber;
    private String password;
    private String roles;
    private String rights;
    private String department;
    private AccountStatus accountStatus;
    private String twoFactorSecret;
    private Instant twoFactorSecretExpiryTime;
    @ElementCollection
    private Set<String> usedTotpCodes=new HashSet<>();
}
