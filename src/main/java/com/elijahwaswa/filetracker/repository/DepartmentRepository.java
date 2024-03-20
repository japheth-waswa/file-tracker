package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department,String> {
    Department findById(UUID id);
    Page<Department> findAllByName(String name, Pageable pageable);
    void deleteById(UUID id);
    long countById(UUID id);
    long countByName(String name);
}
