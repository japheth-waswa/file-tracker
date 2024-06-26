package com.elijahwaswa.filetracker.controller.department;

import com.elijahwaswa.filetracker.controller.setting.SettingController;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.service.department.DepartmentService;
import com.elijahwaswa.filetracker.util.Helpers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("departments")
@AllArgsConstructor
public class DepartmentController {
    private DepartmentService departmentService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Logger LOGGER  = LoggerFactory.getLogger(DepartmentController.class);

    @GetMapping
    public String departmentListPage(Model model) {
        model.addAttribute("allowDataTable", true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Helpers.setViewModelAttrs(authentication,model);

        return "department/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> departmentsList(Model model, HttpSession session, @RequestParam(required = false) String draw, @RequestParam(required = false) int length, @RequestParam(required = false) int start, @RequestParam(required = false, name = "search[value]") String searchValue) {
        List<List<String>> data = null;
        List<Department> departments = null;
        try {
            departments = departmentService.fetchDepartments(start / length, length);
            //variables to be used in the jsp
            Map<String, Object> variables = new HashMap<>();
            data = departments.isEmpty() ? new ArrayList<>() : departments
                    .stream()
                    .map(department -> {
                        variables.put("department", department);

                        List<String> row = new ArrayList<>();
                        row.add(department.getName());
                        row.add(Helpers.loadJspIntoString(request, response, "department/action.jsp", variables));
                        return row;
                    }).toList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //return payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", departments != null ? departments.size() : 0);
        payload.put("recordsFiltered", departmentService.countDepartments());
        return payload;
    }

    private String manageDepartment(Model model, Department customDepartment) {
        return manageDepartment(model, true, customDepartment);
    }

    private String manageDepartment(Model model, boolean resetDepartment, Department customDepartment) {
        if (resetDepartment && customDepartment == null) model.addAttribute("department", new Department());
        if (customDepartment != null) model.addAttribute("department", customDepartment);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Helpers.setViewModelAttrs(authentication,model);

        return "department/manage";
    }

    @GetMapping("manage")
    public String departmentManage(Model model, @RequestParam(required = false) UUID id) {
        Department department = null;
        if (id != null) {
            department = departmentService.fetchDepartment(id);
        }
        return manageDepartment(model, department);
    }

    @PostMapping("manage")
    public String modifyDepartment(Model model, @Valid Department department, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return manageDepartment(model, false, null);
        }

        try {
            if (department.getId() != null) {
                departmentService.updateDepartment(department);
            } else {
                departmentService.saveDepartment(department);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:/departments";
    }

    @GetMapping("delete")
    public String deleteDepartment(Model model, @RequestParam UUID id) {
        departmentService.deleteDepartment(id);
        return "redirect:/departments";
    }
}
