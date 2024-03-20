package com.elijahwaswa.filetracker.dto;

import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.validator.ValidAccountStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserDto {
    private UUID id;

    @Size(min = 1,message = "ID Number is required")
    private String idNumber;

    @Size(min=1,message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @Size(min=1,message = "Roles are required")
    private String roles;

    private String rights;
    private String password;

    private UUID departmentId;
    private String department;

    @ValidAccountStatus
    private AccountStatus accountStatus;

    @Size(min=1,message = "First name is required")
    private String firstName;

    private String middleName;

    @Size(min=1,message = "Other names are required")
    private String otherNames;

    private Instant resetPasswordExpiryTime;
    private String resetPasswordToken;
}
