package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.util.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    User findByIdNumber(String idNumber);

    User findById(UUID id);
    User findByResetPasswordToken(String resetPasswordToken);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.department = ?2 WHERE u.departmentId = ?1")
    void updateAllWithDepartmentId(UUID departmentId, String departmentName);

    Page<User> findAllByIdNumber(String idNumber, Pageable pageable);

    Page<User> findAllByAccountStatus(AccountStatus accountStatus, Pageable pageable);

    Page<User> findAllByDepartment(String department, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.roles LIKE %:userRole%")
    Page<User> findAllByRoles(String userRole, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.rights LIKE %:userRight%")
    Page<User> findAllByRights(String userRight, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.middleName LIKE %?1% OR u.otherNames LIKE %?1%")
    Page<User> findAllByName(String name, Pageable pageable);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = ?1 and u.idNumber != :#{T(com.elijahwaswa.filetracker.util.Helpers).SU_ADMIN_ID}")
    void deleteById(UUID id);

    long countByIdNumber(String idNumber);

    long countById(UUID id);

    long countByAccountStatus(AccountStatus accountStatus);

    @Query("SELECT count(u) FROM User u WHERE u.roles LIKE %:userRole%")
    long countByRoles(String userRole);

    @Query("SELECT count(u) FROM User u WHERE u.rights LIKE %:userRight%")
    long countByRights(String userRight);

    @Query("SELECT count(u) FROM User u WHERE u.firstName LIKE %?1% OR u.middleName LIKE %?1% OR u.otherNames LIKE %?1%")
    long countByName(String name);
}
