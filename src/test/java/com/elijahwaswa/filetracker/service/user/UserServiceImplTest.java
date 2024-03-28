package com.elijahwaswa.filetracker.service.user;


import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserService userService;

    private UserDto userDto1, userDto2, userDto3;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"users"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }


    @BeforeEach
    void setup() {
        truncateH2DB();
        userDto1 = new UserDto();
        userDto1.setIdNumber("1234");
        userDto1.setRoles(String.join(",", List.of(UserRole.SU.name())));
        userDto1.setAccountStatus(AccountStatus.DELETED);
        userDto1.setFirstName("jeff");
        userDto1.setMiddleName("elijah");
        userDto1.setOtherNames("waswa");

        userDto2 = new UserDto();
        userDto2.setIdNumber("4321");
        userDto2.setRoles(String.join(",", List.of(UserRole.SU.name())));
        userDto2.setRights(String.join(",", List.of(UserRight.SUPERVISOR.name())));
        userDto2.setDepartment("registrar");
        userDto2.setAccountStatus(AccountStatus.ACTIVE);
        userDto2.setFirstName("carol");
        userDto2.setMiddleName("mathew");
        userDto2.setOtherNames("katunge");

        userDto3 = new UserDto();
        userDto3.setIdNumber("0987");
        userDto3.setRoles(String.join(",", List.of(UserRole.ADMIN.name(), UserRole.USER.name())));
        userDto3.setRights(String.join(",", List.of(UserRight.SUPERVISOR.name())));
        userDto3.setDepartment("booking");
        userDto3.setAccountStatus(AccountStatus.ACTIVE);
        userDto3.setFirstName("nagel");
        userDto3.setMiddleName("katunge");
        userDto3.setOtherNames("lilcot");
    }

    @Test
    void saveUser() {
        UserDto savedUser = userService.saveUser(userDto2);
        System.out.println(savedUser);
        assertNotNull(savedUser);
        assertEquals(userDto2.getIdNumber(), savedUser.getIdNumber());
    }

    @Test
    void fetchUser_idNumber() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        UserDto user = userService.fetchUser(userDto2.getIdNumber());
        System.out.println(user);
        assertEquals(userDto2.getIdNumber(), user.getIdNumber());
    }

    @Test
    void testFetchUser_id() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        UserDto savedUser = userService.saveUser(userDto3);
        UserDto user = userService.fetchUser(savedUser.getId());
        System.out.println(user);
        assertEquals(userDto3.getIdNumber(), user.getIdNumber());
    }

    @Test
    void testFetchUser_id_fail() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUser(UUID.randomUUID()));
    }

    @Test
    void fetchUsers_pageable() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsers(0, 10);
        System.out.println(users);
        assertEquals(3, users.size());

        List<UserDto> users2 = userService.fetchUsers(1, 2);
        System.out.println(users2);
        assertEquals(1, users2.size());
        assertEquals(userDto1.getIdNumber(), users2.getFirst().getIdNumber());

        List<UserDto> users3 = userService.fetchUsers(1, 1);
        System.out.println(users3);
        assertEquals(1, users3.size());
        assertEquals(userDto2.getIdNumber(), users3.getFirst().getIdNumber());
    }

    @Test
    void fetchUsers_pageable_failed() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsers(5, 10));
    }

    @Test
    void fetchUsers_idNumber_failed() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsers(0, 10, "5555"));
    }

    @Test
    void fetchUsers_idNumber_success() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsers(0, 10, userDto2.getIdNumber());
        System.out.println(users);
        assertEquals(1, users.size());
        assertEquals(userDto2.getIdNumber(), users.getFirst().getIdNumber());
    }

    @Test
    void fetchUsersByAccountStatus() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByAccountStatus(0, 10, AccountStatus.ACTIVE);
        assertEquals(2, users.size());
        assertEquals(userDto3.getIdNumber(), users.getFirst().getIdNumber());
        assertEquals(userDto2.getIdNumber(), users.get(1).getIdNumber());
    }

    @Test
    void fetchUsersByAccountStatus_failed() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsersByAccountStatus(0, 10, AccountStatus.INACTIVE));
    }

    @Test
    void fetchUsersByDepartment() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByDepartment(0, 10, "booking");
        assertEquals(1, users.size());
        assertEquals(userDto3.getIdNumber(), users.getFirst().getIdNumber());
    }

    @Test
    void fetchUsersByDepartment_failed() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsersByDepartment(0, 10, "land admin"));
    }

    @Test
    void fetchUsersByNames() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByNames(0, 10, "katunge");
        System.out.println(users);
        List<UserDto> users1 = userService.fetchUsersByNames(0, 10, "lilcot");
        System.out.println(users1);
    }

    @Test
    void fetchUsersByNames_failed() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsersByNames(0, 10, "mac royce"));
    }

    @Test
    void fetchUsersByRole() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByRole(0, 10, UserRole.SU);
        System.out.println(users);
        assertEquals(2, users.size());
        assertEquals(userDto1.getIdNumber(), users.get(1).getIdNumber());
    }

    @Test
    void fetchUsersByRole_failed() {
        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUsersByRole(0, 10, UserRole.USER));
    }

    @Test
    void fetchUsersByRight() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByRight(0, 10, UserRight.SUPERVISOR);
        System.out.println(users);
        assertEquals(2, users.size());
        assertEquals(userDto2.getIdNumber(), users.get(1).getIdNumber());
    }

    @Test
    void countByIdNumber() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(1, userService.countByIdNumber(userDto2.getIdNumber()));
        assertEquals(0, userService.countByIdNumber("gyc"));
    }

    @Test
    void countById() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        UserDto savedUser = userService.saveUser(userDto3);
        assertEquals(1, userService.countById(savedUser.getId()));
        assertEquals(0, userService.countById(UUID.randomUUID()));
    }

    @Test
    void countByAccountStatus() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(2, userService.countByAccountStatus(AccountStatus.ACTIVE));
        assertEquals(0, userService.countByAccountStatus(AccountStatus.SUSPENDED));
    }

    @Test
    void countByRoles() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(2, userService.countByRoles(UserRole.SU));
        assertEquals(1, userService.countByRoles(UserRole.ADMIN));
    }

    @Test
    void countByRights() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(2, userService.countByRights(UserRight.SUPERVISOR));
    }

    @Test
    void countByName() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(2, userService.countByName("katunge"));
        assertEquals(0, userService.countByName("simiyu"));
        assertEquals(1, userService.countByName("waswa"));
    }

    @Test
    void count() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        assertEquals(3, userService.count());
    }

    @Test
    void updateUser() {
        UserDto savedUser1 = userService.saveUser(userDto1);
        UserDto savedUser2 = userService.saveUser(userDto2);
        UserDto savedUser3 = userService.saveUser(userDto3);
        String middleName = "fulford road";
        savedUser2.setMiddleName(middleName);
        UserDto updatedUser3 = userService.updateUser(savedUser2);
        assertEquals(middleName, updatedUser3.getMiddleName());
    }

    @Test
    void deleteUser() {
        UserDto savedUser1 = userService.saveUser(userDto1);
        UserDto savedUser2 = userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        userService.deleteUser(savedUser1.getId());
        userService.deleteUser(savedUser2.getId());
        List<UserDto> users = userService.fetchUsers(0, 10);
        assertEquals(1, users.size());
        assertEquals(userDto3.getIdNumber(), users.getFirst().getIdNumber());
    }

    @Test
    void fetchUserByResetPasswordToken() {
        userDto1.setResetPasswordToken(UUID.randomUUID().toString());
        userDto2.setResetPasswordToken(UUID.randomUUID().toString());
        userDto3.setResetPasswordToken(UUID.randomUUID().toString());
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        UserDto userDto = userService.fetchUserByResetPasswordToken(userDto2.getResetPasswordToken());
        System.out.println(userDto);
        assertEquals(userDto2.getIdNumber(), userDto.getIdNumber());
    }

    @Test
    void fetchUsersByRoleAndAccountStatus() {
        userService.saveUser(userDto1);
        userService.saveUser(userDto2);
        userService.saveUser(userDto3);
        List<UserDto> users = userService.fetchUsersByRoleAndAccountStatus(0, 10, UserRole.SU,AccountStatus.ACTIVE);
        System.out.println(users);
        assertEquals(1, users.size());
        assertEquals(userDto2.getIdNumber(), users.getFirst().getIdNumber());
        assertThrows(ResourceNotFoundException.class,()->userService.fetchUsersByRoleAndAccountStatus(0, 10, UserRole.SU,AccountStatus.INACTIVE));
    }
}