package com.elijahwaswa.filetracker.service.department;

import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department saveDepartment(Department department);
    Department updateDepartment(Department department);
    Department fetchDepartment(UUID id) throws ResourceNotFoundException;
    List<Department> fetchDepartments(int pageNumber, int pageSize) throws ResourceNotFoundException;
    List<Department> fetchDepartments(int pageNumber,int pageSize,String name) throws ResourceNotFoundException;
    boolean deleteDepartment(UUID id) throws ResourceNotFoundException;
    long countDepartments();
    long countByName(String name);
}
