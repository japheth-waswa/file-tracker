package com.elijahwaswa.filetracker.service.department;

import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.repository.DepartmentRepository;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.Helpers;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;
    private ModelMapper modelMapper;
//    private UserService userService;

    @Override
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Department department) {
        Department departmentRecord = departmentRepository.findById(department.getId());
        if (departmentRecord == null)
            throw new ResourceNotFoundException("Department with id " + department.getId() + " not found");

        //configure ModelMapper to skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        //Map non-null fields
        modelMapper.map(department, departmentRecord);

        //save the updated record
        Department updatedDepartment = departmentRepository.save(departmentRecord);

        //updated department in users
//        userService.updateUsersDepartment(updatedDepartment.getId(),updatedDepartment.getName());

        return updatedDepartment;
    }

    @Override
    public Department fetchDepartment(UUID id) throws ResourceNotFoundException {
        Department department = departmentRepository.findById(id);
        if (department == null) throw new ResourceNotFoundException("Department with id " + id + " not found");
        return department;
    }

    @Override
    public List<Department> fetchDepartments(int pageNumber, int pageSize) throws ResourceNotFoundException {
        Page<Department> departments = departmentRepository.findAll(Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(Department.class, departments, "No departments found");
    }

    @Override
    public List<Department> fetchDepartments(int pageNumber, int pageSize, String name) throws ResourceNotFoundException {
        Page<Department> departments = departmentRepository.findAllByName(name, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(Department.class, departments, "No departments found");
    }

    @Override
    @Transactional
    public boolean deleteDepartment(UUID id) throws ResourceNotFoundException {
        departmentRepository.deleteById(id);
        return true;
    }

    @Override
    public long countDepartments() {
        return departmentRepository.count();
    }

    @Override
    public long countByName(String name) {
        return departmentRepository.countByName(name);
    }
}
