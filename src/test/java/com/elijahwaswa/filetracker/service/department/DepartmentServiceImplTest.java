package com.elijahwaswa.filetracker.service.department;

import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DepartmentServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DepartmentService departmentService;

    private Department department1, department2, department3;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"departments"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }

    @BeforeEach
    void setUp() {
        truncateH2DB();

        department1 = new Department();
        department1.setName("Registrar");

        department2 = new Department();
        department2.setName("Booking");

        department3 = new Department();
        department3.setName("Valuation");
    }

    @Test
    void saveDepartment() {
        departmentService.saveDepartment(department1);
        Department savedDepartment = departmentService.saveDepartment(department2);
        departmentService.saveDepartment(department3);
        assertEquals(department2.getName(), savedDepartment.getName());
    }

    @Test
    void updateDepartment() {
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department2);
        Department savedDepartment = departmentService.saveDepartment(department3);
        String name = "Land administration";
        savedDepartment.setName(name);
        departmentService.updateDepartment(savedDepartment);
        //fetch the department
        Department department = departmentService.fetchDepartment(savedDepartment.getId());
        assertEquals(savedDepartment.getName(), department.getName());
    }

    @Test
    void fetchDepartment() {
        departmentService.saveDepartment(department1);
        Department savedDepartment = departmentService.saveDepartment(department3);
        Department department = departmentService.fetchDepartment(savedDepartment.getId());
        assertEquals(department3.getName(), department.getName());
    }

    @Test
    void fetchDepartments() {
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department3);
        departmentService.saveDepartment(department2);
        List<Department> departments = departmentService.fetchDepartments(0, 10);
        assertEquals(3,departments.size());
        assertEquals(department1.getName(),departments.get(2).getName());
    }

    @Test
    void FetchDepartments_by_name() {
        departmentService.saveDepartment(department2);
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department3);
        List<Department> departments = departmentService.fetchDepartments(0, 10,department1.getName());
        assertEquals(1,departments.size());
        assertEquals(department1.getName(),departments.getFirst().getName());
    }

    @Test
    void deleteDepartment() {
        departmentService.saveDepartment(department2);
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department3);
        //delete department2
        departmentService.deleteDepartment(department2.getId());
        List<Department> departments = departmentService.fetchDepartments(0, 10);
        assertEquals(2,departments.size());
        assertEquals(department1.getName(),departments.get(1).getName());
    }

    @Test
    void countDepartments() {
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department3);
        assertEquals(2,departmentService.countDepartments());
    }

    @Test
    void countByName() {
        departmentService.saveDepartment(department2);
        departmentService.saveDepartment(department1);
        departmentService.saveDepartment(department3);
        assertEquals(1,departmentService.countByName(department3.getName()));
    }
}