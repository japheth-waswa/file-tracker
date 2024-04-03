package com.elijahwaswa.filetracker.service.user;

import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.exception.ex.InternalException;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.util.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto saveUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    UserDto fetchUser(String idNumber) throws ResourceNotFoundException;
    UserDto fetchUser(UUID id) throws ResourceNotFoundException;
    UserDto fetchUserByResetPasswordToken(String resetPasswordToken) throws ResourceNotFoundException;
    List<UserDto> fetchUsers(int pageNumber,int pageSize) throws ResourceNotFoundException;
    List<UserDto> fetchUsers(int pageNumber,int pageSize,String idNumber) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByRole(int pageNumber, int pageSize, UserRole userRole) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByRoleAndAccountStatus(int pageNumber, int pageSize, UserRole userRole,AccountStatus accountStatus) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByRight(int pageNumber, int pageSize, UserRight userRight) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByAccountStatus(int pageNumber, int pageSize, AccountStatus accountStatus) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByDepartment(int pageNumber, int pageSize,String department) throws ResourceNotFoundException;
    List<UserDto> fetchUsersByNames(int pageNumber, int pageSize,String name) throws ResourceNotFoundException;
    void updateUsersDepartment(UUID departmentId,String departmentName);
    ResetLinkPayload generatePasswordResetLink(String idNumber, String baseURL) throws InternalException, ResourceNotFoundException;
    TwoFactorPayload generateToTpQRCodeBase64Encoded(String idNumber, String appName) throws InternalException, ResourceNotFoundException;
    boolean resetTOTPSecret(String idNumber) throws InternalException, ResourceNotFoundException;
    boolean deleteUser(UUID id);
    long count();
    long countByIdNumber(String idNumber);
    long countById(UUID id);
    long countByAccountStatus(AccountStatus accountStatus);
    long countByRoles(UserRole userRole);
    long countByRights(UserRight userRight);
    long countByName(String name);
}
