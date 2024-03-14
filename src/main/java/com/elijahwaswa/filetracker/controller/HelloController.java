package com.elijahwaswa.filetracker.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("hello")
public class HelloController {

    @GetMapping()
    @ResponseBody
    public String hello(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        return "hello jeff here!=" + department;
    }

    @GetMapping("/su")
    public String helloSu(Model model, HttpSession session, ModelMap modelMap,@RequestParam(required = false) String g) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
//        System.out.println("Super admin  = " + department);
        model.addAttribute("location","nairobi west");
        model.addAttribute("g",g);
        modelMap.put("amount",234);
        return "su";
    }

    @GetMapping("/admin")
    @ResponseBody
    public String helloAdmin(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        return "Normal Admin  = " + department;
    }

    @GetMapping("/user")
    @ResponseBody
    public String helloUser(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        return "User  = " + department;
    }

    @GetMapping("/user-supervisor")
    @ResponseBody
    public String helloUserSupervisor(HttpSession session) {
        String department = session != null ? (String) session.getAttribute("department") : "No department";
        return "User Supervisor = " + department;
    }


}
