package com.elijahwaswa.filetracker.controller.user;

import com.elijahwaswa.filetracker.controller.file.FileController;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.model.Department;
import com.elijahwaswa.filetracker.service.department.DepartmentService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.elijahwaswa.filetracker.util.Helpers.parseAuthenticatedRoles;

@Controller
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private DepartmentService departmentService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public String users(Model model) {
        model.addAttribute("allowDataTable", true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Helpers.setViewModelAttrs(authentication, model);

        return "user/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> usersList(Model model, HttpSession session, @RequestParam(required = false) String draw, @RequestParam(required = false) int length, @RequestParam(required = false) int start, @RequestParam(required = false, name = "search[value]") String searchValue) {

        List<List<String>> data = null;
        List<UserDto> users = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<UserRole> roles = parseAuthenticatedRoles(authentication);
        boolean isSu = roles.contains(UserRole.SU);

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
                        variables.put("isSu", isSu);

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
        payload.put("recordsTotal", users != null ? users.size() : 0);
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
        try {
            model.addAttribute("departments", departmentService.fetchDepartments(0, 500));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Helpers.setViewModelAttrs(authentication, model);

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

    @PostMapping("reset-link")
    @ResponseBody
    public Map<String, String> generateResetLink(@RequestParam String idNumber) {
        Map<String, String> data = new HashMap<>();

        if (idNumber == null || idNumber.isBlank()) {
            data.put("status", "403");
            data.put("message", "idNumber must be provided!");
            return data;
        }

        ResetLinkPayload resetLinkPayload;
        try {
            resetLinkPayload = userService.generatePasswordResetLink(idNumber, Helpers.generateBaseUrl(request));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            data.put("status", "403");
            data.put("message", "Internal error occurred");
            return data;
        }

        data.put("status", "200");
        data.put("message", "successful");
        data.put("resetLink", resetLinkPayload.resetLink());
        return data;
    }

    @GetMapping("reset-totp")
    public String resetTOTP() {
        try {
            String idNumber = Helpers.getLoggedInUsername();
            userService.resetTOTPSecret(idNumber);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:" + Helpers.AUTHENTICATED_ROOT_URL;
    }

    @GetMapping("delete")
    public String deleteUser(Model model, @RequestParam UUID id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
