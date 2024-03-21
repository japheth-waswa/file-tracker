package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department,String> {
    Department findById(UUID id);
    Department findByName(String name);
    Page<Department> findAllByName(String name, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Department d WHERE d.id = ?1 and d.name != :#{T(com.elijahwaswa.filetracker.util.Helpers).DEFAULT_DEPARTMENT_NAME}")
    void deleteById(UUID id);
    long countById(UUID id);
    long countByName(String name);
}
