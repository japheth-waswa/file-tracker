package com.elijahwaswa.filetracker.controller;

import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("hello")
public class HelloController {

    private void showAuthRoles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        System.out.println(roles);
        System.out.println(UserRole.USER.name() + " = " + roles.contains(Helpers.ROLE_PREPEND +UserRole.SU.name()));
        System.out.println(UserRole.USER.name() + " = " + roles.contains(Helpers.ROLE_PREPEND +UserRole.USER.name()));
        System.out.println(UserRight.SUPERVISOR.name() + " = " + roles.contains(UserRight.SUPERVISOR.name()));

    }

    @GetMapping()
    @ResponseBody
    public String hello(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        return "hello jeff here!=" + department;
    }

    @GetMapping("/su")
    public String helloSu(Model model, HttpSession session, ModelMap modelMap, @RequestParam(required = false) String g) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
//        System.out.println("Super admin  = " + department);
        model.addAttribute("location", "nairobi west");
        model.addAttribute("g", g);
        modelMap.put("amount", 234);
        showAuthRoles();
        return "hello/su";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String helloAdmin(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        showAuthRoles();
        return "Normal Admin  = " + department;
    }

    @GetMapping("/user")
    @ResponseBody
    public String helloUser(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        showAuthRoles();
        return "User  = " + department;
    }

    @GetMapping("/user-supervisor")
    public String helloUserSupervisor(Model model, HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
//        return "User Supervisor = " + department;
        model.addAttribute("allowDataTable", true);
        showAuthRoles();
        return "hello/supervisor";
    }

    @PostMapping("/data-list")
    @ResponseBody
    public Map<String, Object> dataList(Model model, HttpSession session, @RequestParam(required = false) String draw, @RequestParam(required = false) int length, @RequestParam(required = false) int start, @RequestParam(required = false, name = "search[value]") String searchValue, @RequestParam(required = false, name = "search[regex]") boolean searchRegex, @RequestParam String location) {

        int limit = length;
        int offset = start;
        System.out.println(limit);
        System.out.println(offset);
        System.out.println("searchValue= " + searchValue);
        System.out.println("searchRegex= " + searchRegex);
        System.out.println("location= " + location);

        List<List<String>> data = List.of(List.of("john", "doe"), List.of("jane", "doe"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", 2 + "");
        payload.put("recordsFiltered", 2 + "");
        showAuthRoles();
        return payload;
    }

}