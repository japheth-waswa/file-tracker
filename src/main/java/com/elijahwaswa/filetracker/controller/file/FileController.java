package com.elijahwaswa.filetracker.controller.file;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.service.file.FileService;
import com.elijahwaswa.filetracker.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/files")
public class FileController {
    private FileService fileService;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @GetMapping
    public String listFilesPage(Model model) {
        model.addAttribute("allowDataTable", true);
        model.addAttribute("manageAllowed", manageAllowed());
        return "file/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> usersList(Model model, HttpSession session, @RequestParam(required = false) String draw, @RequestParam(required = false) int length, @RequestParam(required = false) int start, @RequestParam(required = false, name = "search[value]") String searchValue) {

        List<List<String>> data = null;
        List<FileDto> files = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean deleteAllowed = Helpers.parseAuthenticatedRoles(authentication).contains(UserRole.SU);

        try {
            //fetch
            files = fileService.fetchFiles(start / length, length);

            //variables to be used in the jsp
            Map<String, Object> variables = new HashMap<>();

            //transform List<FileDto> to List<List<String>> for DataTables
            data = files.isEmpty() ? new ArrayList<>() : files
                    .stream()
                    .map(file -> {
                        variables.put("file", file);
                        variables.put("deleteAllowed", deleteAllowed);
                        variables.put("manageAllowed", manageAllowed());

                        List<String> row = new ArrayList<>();
                        row.add(file.getLrNo());
                        row.add(file.getIrNo());
                        row.add(file.getCfNo());
                        row.add(String.valueOf(file.getAreaSize()));
                        row.add(file.getFileNature() != null ? file.getFileNature().name() : "");
                        row.add(file.getFileStatus() != null ? file.getFileStatus().name() : "");
                        row.add(file.getCurrentUserFullNames());
                        row.add(file.getCurrentDepartment());
                        row.add(file.getCreatedBy());
                        row.add(Helpers.loadJspIntoString(request, response, "file/action.jsp", variables));
                        return row;
                    }).toList();
        } catch (Exception e) {
        }

        long total = fileService.count();

        //return payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", files != null ? files.size() : 0);
        payload.put("recordsFiltered", total);

        return payload;
    }

    private String managePage(Model model, FileDto customFileDto) {
        return managePage(model, true, customFileDto);
    }

    private String managePage(Model model, boolean resetFileDto, FileDto customFileDto) {
        if (resetFileDto && customFileDto == null) model.addAttribute("fileDto", new FileDto());
        if (customFileDto != null) model.addAttribute("fileDto", customFileDto);

        model.addAttribute("fileStatuses", FileStatus.values());
        model.addAttribute("fileNatures", FileNature.values());
        model.addAttribute("manageAllowed", manageAllowed());
        return "file/manage";
    }

    private boolean manageAllowed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<UserRole> userRoles = Helpers.parseAuthenticatedRoles(authentication);
        Set<UserRight> userRights = Helpers.parseAuthenticatedRights(authentication);
        System.out.println("userRoles = " + userRoles);
        System.out.println("userRights = " + userRights);
        return userRoles.contains(UserRole.SU) || userRoles.contains(UserRole.ADMIN) || (userRoles.contains(UserRole.USER) && userRights.contains(UserRight.SUPERVISOR));
    }

    @GetMapping("manage")
    public String fileManage(Model model, @RequestParam(required = false) UUID id) {
        FileDto fileDto = null;
        if (id != null) {
            fileDto = fileService.fetchFile(id);
        }
        return managePage(model, fileDto);
    }

    @PostMapping("manage")
    public String modifyFile(Model model, HttpSession session, @Valid FileDto fileDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return managePage(model, false, null);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<UserRole> userRoles = Helpers.parseAuthenticatedRoles(authentication);
        Set<UserRight> userRights = Helpers.parseAuthenticatedRights(authentication);

        String department = session != null ? (String) session.getAttribute("department") : null;
        String fullNames = session != null ? (String) session.getAttribute("fullNames") : null;
        String idNumber = session != null ? (String) session.getAttribute("idNumber") : null;
        try {
            if (fileDto.getId() != null) {
                fileService.updateFile(fileDto, idNumber, fullNames, department, userRoles.stream().toList(), userRights.stream().toList());
            } else {
                fileService.saveFile(fileDto, idNumber, fullNames, department);
            }
        } catch (Exception e) {
        }
        return "redirect:/files";
    }

    @GetMapping("delete")
    public String deleteFile(Model model, @RequestParam UUID id) {
        fileService.deleteFile(id);
        return "redirect:/files";
    }
}
