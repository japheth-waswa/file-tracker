package com.elijahwaswa.filetracker.service.user;

import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.service.department.DepartmentService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private DepartmentService departmentService;

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            //fetch the department
            UUID departmentId = userDto.getDepartmentId();
            if (departmentId != null) {
                Department department = departmentService.fetchDepartment(departmentId);
                userDto.setDepartment(department.getName());
            }
        } catch (Exception e) {
        }
        //set default password
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(Helpers.USER_DEFAULT_PASSWORD));

        //save the user
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId());
        if (user == null) throw new ResourceNotFoundException("User with id " + userDto.getId() + " not found");

        try {
            //fetch the department
            UUID departmentId = userDto.getDepartmentId();
            if (departmentId != null) {
                Department department = departmentService.fetchDepartment(departmentId);
                userDto.setDepartment(department.getName());
            }
        } catch (Exception e) {
        }

        //if Helpers.SU_ADMIN_ID is being updated, then disable some fields from update.
        if (user.getIdNumber().equalsIgnoreCase(Helpers.SU_ADMIN_ID)) {
            userDto.setIdNumber(user.getIdNumber());
            userDto.setRoles(user.getRoles());
            userDto.setRights(user.getRights());
            userDto.setEmail(user.getEmail());
            userDto.setAccountStatus(AccountStatus.ACTIVE);
        }

        //configure ModelMapper to skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        //Map non-null fields from userDto to user
        modelMapper.map(userDto, user);

        //save the updated user
        User updatedUser = userRepository.save(user);

        //return updated user as UserDto
        return modelMapper.map(updatedUser, UserDto.class);

    }

    @Override
    public UserDto fetchUser(String idNumber) {
        User user = userRepository.findByIdNumber(idNumber);
        if (user == null) throw new ResourceNotFoundException("User with id number " + idNumber + " not found");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto fetchUserByResetPasswordToken(String resetPasswordToken) throws ResourceNotFoundException {
        User user = userRepository.findByResetPasswordToken(resetPasswordToken);
        if (user == null)
            throw new ResourceNotFoundException("User with reset password token " + resetPasswordToken + " not found");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto fetchUser(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) throw new ResourceNotFoundException("User with id " + id + " not found");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> fetchUsers(int pageNumber, int pageSize) {
        Page<User> users = userRepository.findAll(Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(UserDto.class, users, "No users found");
    }

    @Override
    public List<UserDto> fetchUsers(int pageNumber, int pageSize, String idNumber) {
        Page<User> users = userRepository.findAllByIdNumber(idNumber, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(UserDto.class, users, "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByRole(int pageNumber, int pageSize, UserRole userRole) {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByRoles(userRole.name(), Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByRoleAndAccountStatus(int pageNumber, int pageSize, UserRole userRole, AccountStatus accountStatus) throws ResourceNotFoundException {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByRolesAndAccountStatus(userRole.name(), accountStatus, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByRight(int pageNumber, int pageSize, UserRight userRight) {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByRights(userRight.name(), Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByAccountStatus(int pageNumber, int pageSize, AccountStatus accountStatus) {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByAccountStatus(accountStatus, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByDepartment(int pageNumber, int pageSize, String department) {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByDepartment(department, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public List<UserDto> fetchUsersByNames(int pageNumber, int pageSize, String name) {
        return Helpers.parsePageableRecordsToList(UserDto.class,
                userRepository.findAllByName(name, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt")))),
                "No users found");
    }

    @Override
    public void updateUsersDepartment(UUID departmentId, String departmentName) {
        userRepository.updateAllWithDepartmentId(departmentId, departmentName);
    }

    @Override
    @Transactional
    public boolean deleteUser(UUID id) {
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public long countByIdNumber(String idNumber) {
        return userRepository.countByIdNumber(idNumber);
    }

    @Override
    public long countById(UUID id) {
        return userRepository.countById(id);
    }

    @Override
    public long countByAccountStatus(AccountStatus accountStatus) {
        return userRepository.countByAccountStatus(accountStatus);
    }

    @Override
    public long countByRoles(UserRole userRole) {
        return userRepository.countByRoles(userRole.name());
    }

    @Override
    public long countByRights(UserRight userRight) {
        return userRepository.countByRights(userRight.name());
    }

    @Override
    public long countByName(String name) {
        return userRepository.countByName(name);
    }
}
