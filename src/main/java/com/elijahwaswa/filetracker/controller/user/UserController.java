package com.elijahwaswa.filetracker.controller.user;

import com.elijahwaswa.filetracker.controller.file.FileController;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.service.department.DepartmentService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private DepartmentService departmentService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Logger LOGGER  = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public String users(Model model) {
        model.addAttribute("allowDataTable", true);
        return "user/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> usersList(Model model, HttpSession session, @RequestParam(required = false) String draw, @RequestParam(required = false) int length, @RequestParam(required = false) int start, @RequestParam(required = false, name = "search[value]") String searchValue) {
        //todo fetch the uses list with Pageable,filter(idNumber,roles,rights,department,accountStatus,names)

        List<List<String>> data = null;
        List<UserDto> users = null;

        try {
            //fetch & count users
            users = userService.fetchUsers(start / length, length);

            //variables to be used in the jsp
            Map<String, Object> variables = new HashMap<>();

            //transform List<UserDto> to List<List<String>> for DataTables
            data = users.isEmpty() ? new ArrayList<>() : users
                    .stream()
                    .map(user -> {
                        String fullNames = user.getFirstName() + " " + user.getMiddleName() + " " + user.getOtherNames();
                        variables.put("user", user);
                        variables.put("fullNames", fullNames);

                        List<String> userRow = new ArrayList<>();
                        userRow.add(user.getIdNumber());
                        userRow.add(fullNames);
                        userRow.add(user.getEmail());
                        userRow.add(user.getDepartment());
                        userRow.add(user.getRoles());
                        userRow.add(user.getRights());
                        userRow.add(user.getAccountStatus() != null ? user.getAccountStatus().name() : "");
                        userRow.add(Helpers.loadJspIntoString(request, response, "user/action.jsp", variables));
                        return userRow;
                    }).toList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //return payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", users != null ?users.size():0);
        payload.put("recordsFiltered", userService.count());

        return payload;
    }

    private String managePage(Model model, UserDto customUserDto) {
        return managePage(model, true, customUserDto);
    }

    private String managePage(Model model, boolean resetUserDto, UserDto customUserDto) {
        if (resetUserDto && customUserDto == null) model.addAttribute("userDto", new UserDto());
        if (customUserDto != null) model.addAttribute("userDto", customUserDto);

        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("userRights", UserRight.values());
        model.addAttribute("accountStatuses", AccountStatus.values());
        try{
            model.addAttribute("departments", departmentService.fetchDepartments(0,500));
        }catch(Exception e){
            LOGGER.error(e.getMessage());
        }

        return "user/manage";
    }

    @GetMapping("manage")
    public String userManage(Model model, @RequestParam(required = false) UUID id) {
        UserDto userDto = null;
        if (id != null) {
            userDto = userService.fetchUser(id);
        }
        return managePage(model, userDto);
    }

    @PostMapping("manage")
    public String modifyUser(Model model, @Valid UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return managePage(model, false, null);
        }

        if (userDto.getId() != null) {
            userService.updateUser(userDto);
        } else {
            userService.saveUser(userDto);
        }
        return "redirect:/users";
    }

    @GetMapping("delete")
    public String deleteUser(Model model, @RequestParam UUID id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
