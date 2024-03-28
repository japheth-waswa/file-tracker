package com.elijahwaswa.filetracker.controller.file;

import com.elijahwaswa.filetracker.consumer.ReminderConsumer;
import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.dto.FileTrailDto;
import com.elijahwaswa.filetracker.dto.UserDto;
import com.elijahwaswa.filetracker.event.ReminderEvent;
import com.elijahwaswa.filetracker.publisher.ReminderProducer;
import com.elijahwaswa.filetracker.service.file.FileService;
import com.elijahwaswa.filetracker.service.file.FileTrailService;
import com.elijahwaswa.filetracker.service.user.UserService;
import com.elijahwaswa.filetracker.util.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@AllArgsConstructor
@Controller
@RequestMapping("/files")
public class FileController {
    private UserService userService;
    private FileService fileService;
    private FileTrailService fileTrailService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReminderProducer reminderProducer;
    private final ExecutorService executorService= Executors.newSingleThreadExecutor();
    private final Logger LOGGER  = LoggerFactory.getLogger(FileController.class);

    @GetMapping
    public String listFilesPage(Model model) {
        model.addAttribute("allowDataTable", true);
        model.addAttribute("manageAllowed", manageAllowed());
        model.addAttribute("fileStatuses", FileStatus.values());
        model.addAttribute("fileSearchFilters", FileSearchFilter.values());
        return "file/list";
    }

    @PostMapping
    @ResponseBody
    public Map<String, Object> usersList(Model model, HttpSession session,
                                         @RequestParam(required = false) String draw,
                                         @RequestParam(required = false) int length,
                                         @RequestParam(required = false) int start,
                                         @RequestParam(required = false, name = "search[value]") String searchValue,
                                         @RequestParam(required = false) FileSearchFilter fileSearchFilter,
                                         @RequestParam(required = false) FileStatus fileStatuses,
                                         @RequestParam(required = false) String filterSearchValue) {

        List<List<String>> data = null;
        List<FileDto> files = null;
        long total = 0;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean deleteAllowed = Helpers.parseAuthenticatedRoles(authentication).contains(UserRole.SU);
        String idNumber = authentication.getName();

        try {
            int pageNumber = start / length;

            if (fileSearchFilter == FileSearchFilter.LR_NO && filterSearchValue != null && !filterSearchValue.isBlank()) {
                files = fileService.fetchFiles(pageNumber, length, filterSearchValue.trim());
                total = fileService.countByLrNo(filterSearchValue.trim());
            } else if (fileSearchFilter == FileSearchFilter.FILE_STATUS && fileStatuses != null) {
                files = fileService.fetchFiles(pageNumber, length, fileStatuses);
                total = fileService.countByFileStatus(fileStatuses);
            } else if (fileSearchFilter == FileSearchFilter.MY_FILES) {
                files = fileService.fetchFiles(idNumber, pageNumber, length);
                total = fileService.countByCurrentUserIdNumber(idNumber);
            } else {
                files = fileService.fetchFiles(pageNumber, length);
                total = fileService.count();
            }

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
                        FileStatus currentFileStatus = file.getFileStatus();
                        row.add(currentFileStatus != null ? currentFileStatus.equals(FileStatus.LOCKED) ? "<span class='badge text-bg-danger'>" + currentFileStatus.name() + "</span>" : currentFileStatus.name() : "");
                        row.add(file.getCurrentUserFullNames());
                        row.add(file.getCurrentDepartment());
                        row.add(file.getCreatedBy());
                        row.add(Helpers.loadJspIntoString(request, response, "file/action.jsp", variables));
                        return row;
                    }).toList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


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
        return userRoles.contains(UserRole.SU) || userRoles.contains(UserRole.ADMIN) || (userRoles.contains(UserRole.USER) && userRights.contains(UserRight.SUPERVISOR));
    }

    private boolean manageFileTrail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<UserRole> userRoles = Helpers.parseAuthenticatedRoles(authentication);
        return userRoles.contains(UserRole.SU) || userRoles.contains(UserRole.ADMIN);
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

        String department = session != null ? (String) session.getAttribute("department") : null;
        String fullNames = session != null ? (String) session.getAttribute("fullNames") : null;
        String idNumber = authentication.getName();
        try {
            if (fileDto.getId() != null) {
                fileService.updateFile(fileDto);
            } else {
                fileService.saveFile(fileDto, idNumber, fullNames, department);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "redirect:/files";
    }

    @GetMapping("delete")
    public String deleteFile(Model model, @RequestParam UUID id) {
        fileService.deleteFile(id);
        return "redirect:/files";
    }

    @GetMapping("view")
    public String viewFile(Model model, @RequestParam UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idNumber = authentication.getName();
        boolean dispatchAllowed = manageAllowed();

        FileDto fileDto = fileService.fetchFile(id);
        model.addAttribute("fileDto", fileDto);
        List<UserDto> assignableUsers = null;
        try {
            assignableUsers = userService.fetchUsersByRoleAndAccountStatus(0, 500, UserRole.USER, AccountStatus.ACTIVE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        if (!dispatchAllowed) {
            //check if id's match
            dispatchAllowed = fileDto.getCurrentUserIdNumber().equalsIgnoreCase(idNumber);
        }

        //check if file is locked
        dispatchAllowed = !fileDto.getFileStatus().equals(FileStatus.LOCKED) && dispatchAllowed;

        model.addAttribute("allowDatePicker", true);
        model.addAttribute("allowDataTable", true);
        model.addAttribute("assignableUsers", assignableUsers);
        model.addAttribute("dispatchAllowed", dispatchAllowed);
        model.addAttribute("manageFileTrail", manageFileTrail());
        model.addAttribute("fileTrailDto", new FileTrailDto());
        model.addAttribute("fileTrailOrigins", FileTrailOrigin.values());
        return "file/view";
    }

    @PostMapping("dispatch")
    public String dispatchFile(Model model, HttpSession session, @Valid FileTrailDto fileTrailDto, BindingResult bindingResult, @RequestParam UUID fileId) {
        if (bindingResult.hasErrors()) {
            String formErrors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            return "redirect:/files/view?id=" + fileId + "&error=" + formErrors;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String fullNames = session != null ? (String) session.getAttribute("fullNames") : null;
        String idNumber = authentication.getName();
        boolean genericAllowed = manageAllowed();

        //fetch file
        FileDto fileDto = null;
        try {
            if (genericAllowed) {
                //fetch generally
                fileDto = fileService.fetchFile(fileTrailDto.getLrNo());
            } else {
                //fetch by ensuring this file is currently in this user's account
                fileDto = fileService.fetchFile(fileTrailDto.getLrNo(), idNumber);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:/files/view?id=" + fileId + "&error=You are not allowed to dispatch this file!";
        }

        //check if file is locked,if so, then not allowed
        if (fileDto.getFileStatus().equals(FileStatus.LOCKED))
            return "redirect:/files/view?id=" + fileId + "&error=Locked file cannot be dispatched!";

        //fetch user (must have the USER role)
        UserDto userDto = null;
        String userNotAllowedMessage = "The user cannot be assigned the file!";
        try {
            userDto = userService.fetchUser(fileTrailDto.getAssignedToIdNumber());
            //check has the USER role
            if (!Arrays.stream(userDto.getRoles().split(",")).toList().contains(UserRole.USER.name())) {
                return "redirect:/files/view?id=" + fileId + "&error=" + userNotAllowedMessage;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:/files/view?id=" + fileId + "&error=" + userNotAllowedMessage;
        }

        try {
            //update the previous fileTrail record. Update Record
            List<FileTrailDto> fileTrails = fileTrailService.fetchFileTrails(0, 1, fileTrailDto.getLrNo());
            FileTrailDto existingFileTrailDto = fileTrails.getFirst();
            existingFileTrailDto.setDispatchedOn(LocalDateTime.now());
            existingFileTrailDto.setDispatchedByIdNumber(idNumber);
            existingFileTrailDto.setDispatchedByFullNames(fullNames);
            existingFileTrailDto.setTimeTakenInSeconds(Helpers.timeDifference(ChronoUnit.SECONDS,
                    existingFileTrailDto.getAssignedOn() != null ? existingFileTrailDto.getAssignedOn() : LocalDateTime.now(),
                    LocalDateTime.now()));
            fileTrailService.updateFileTrail(existingFileTrailDto);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //dispatch the file.New Record
        String userDtoFullNames = userDto.getFirstName() + " " + userDto.getMiddleName() + " " + userDto.getOtherNames();
        fileTrailDto.setAssignedToFullNames(userDtoFullNames);
        fileTrailDto.setAssignedByIdNumber(idNumber);
        fileTrailDto.setAssignedByFullNames(fullNames);
        fileTrailDto.setAssignedOn(LocalDateTime.now());
        fileTrailDto.setDepartment(userDto.getDepartment());
        fileTrailDto.setCreatedBy(fullNames);

        //compute dueDate
        fileTrailDto = fileTrailService.computeDueDate(fileTrailDto);
        FileTrailDto savedFileTrail = fileTrailService.saveFileTrail(fileTrailDto);

        //update file
        fileDto.setCurrentDepartment(userDto.getDepartment());
        fileDto.setCurrentUserIdNumber(fileTrailDto.getAssignedToIdNumber());
        fileDto.setCurrentUserFullNames(userDtoFullNames);
        fileService.updateFile(fileDto);

        executorService.execute(()->reminderProducer.sendMessage(
                new ReminderEvent(savedFileTrail.getLrNo(), savedFileTrail.getId(), savedFileTrail.getAssignedToIdNumber(), savedFileTrail.getDepartment())
                ,Helpers.timeDifference(ChronoUnit.MILLIS, LocalDateTime.now(), savedFileTrail.getDueDate())
        ));

        return "redirect:/files/view?id=" + fileId + "&success=File dispatched successfully";
    }

    @PostMapping("file-trail")
    @ResponseBody
    public Map<String, Object> fileTrailList(Model model, HttpSession session,
                                             @RequestParam(required = false) String draw,
                                             @RequestParam(required = false) int length,
                                             @RequestParam(required = false) int start,
                                             @RequestParam(required = false, name = "search[value]") String searchValue,
                                             @RequestParam String lrNo) {
        List<List<String>> data = null;
        List<FileTrailDto> fileTrails = null;
        long total = fileTrailService.countByLrNo(lrNo);

        try {
            int pageNumber = start / length;
            fileTrails = fileTrailService.fetchFileTrails(pageNumber, length, lrNo);

            //variables to be used in the jsp
            Map<String, Object> variables = new HashMap<>();

            //transform List<FileTrailDto> to List<List<String>> for DataTables
            data = fileTrails.isEmpty() ? new ArrayList<>() : fileTrails
                    .stream()
                    .map(fileTrail -> {
                        List<String> row = new ArrayList<>();
                        long timeTaken = fileTrail.getTimeTakenInSeconds() != 0 ? fileTrail.getTimeTakenInSeconds() : Helpers.timeDifference(ChronoUnit.SECONDS, fileTrail.getAssignedOn(), LocalDateTime.now());
                        row.add(fileTrail.getAssignedByFullNames());
                        row.add(fileTrail.getAssignedOn() != null ? Helpers.formatDateTime(fileTrail.getAssignedOn(), Helpers.DATE_TIME_VIEW_FORMAT) : "");
                        row.add(fileTrail.getDepartment());
                        row.add(fileTrail.getAssignedByFullNames());
                        row.add(fileTrail.getDispatchNote());
                        row.add(fileTrail.getDispatchedByFullNames());
                        row.add(fileTrail.getDispatchedOn() != null ? Helpers.formatDateTime(fileTrail.getDispatchedOn(), Helpers.DATE_TIME_VIEW_FORMAT) : "");
                        row.add(Helpers.formatDuration(timeTaken));
                        return row;
                    }).toList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //return payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("draw", draw);
        payload.put("data", data);
        payload.put("recordsTotal", fileTrails != null ? fileTrails.size() : 0);
        payload.put("recordsFiltered", total);

        return payload;
    }

    @PostMapping("add-file_trail")
    public String addFileTrail(Model model, HttpSession session, @Valid FileTrailDto fileTrailDto, BindingResult bindingResult, @RequestParam UUID fileId) {
        if (bindingResult.hasErrors()) {
            String formErrors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            return "redirect:/files/view?id=" + fileId + "&error=" + formErrors;
        }

        String fullNames = session != null ? (String) session.getAttribute("fullNames") : null;

        //fetch file
        FileDto fileDto = null;
        try {
            fileDto = fileService.fetchFile(fileTrailDto.getLrNo());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:/files/view?id=" + fileId + "&error=You are not allowed to dispatch this file!";
        }


        //fetch assignedUser user (must have the USER role)
        UserDto assignedToUser = null;
        String userNotAllowedMessage = "The user cannot be assigned the file!";
        try {
            assignedToUser = userService.fetchUser(fileTrailDto.getAssignedToIdNumber());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return "redirect:/files/view?id=" + fileId + "&error=" + userNotAllowedMessage;
        }

        //fetch assigned by user
        UserDto assignedByUser = null;
        try {
            if (fileTrailDto.getAssignedByIdNumber() != null && !fileTrailDto.getAssignedByIdNumber().isBlank())
                assignedByUser = userService.fetchUser(fileTrailDto.getAssignedByIdNumber());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        //fetch dispatched by user
        UserDto dispatchedByUser = null;
        try {
            if (fileTrailDto.getDispatchedByIdNumber() != null && !fileTrailDto.getDispatchedByIdNumber().isBlank())
                dispatchedByUser = userService.fetchUser(fileTrailDto.getDispatchedByIdNumber());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        fileTrailDto.setAssignedToFullNames(assignedToUser.getFirstName() + " " + assignedToUser.getMiddleName() + " " + assignedToUser.getOtherNames());
        fileTrailDto.setDepartment(assignedToUser.getDepartment());
        fileTrailDto.setAssignedByFullNames(assignedByUser != null ? assignedByUser.getFirstName() + " " + assignedByUser.getMiddleName() + " " + assignedByUser.getOtherNames() : null);
        fileTrailDto.setDispatchedByFullNames(dispatchedByUser != null ? dispatchedByUser.getFirstName() + " " + dispatchedByUser.getMiddleName() + " " + dispatchedByUser.getOtherNames() : null);
        fileTrailDto.setCreatedBy(fullNames);
        fileTrailDto.setCreatedAt(fileTrailDto.getAssignedOn());
        fileTrailDto.setTimeTakenInSeconds(Helpers.timeDifference(ChronoUnit.SECONDS, fileTrailDto.getAssignedOn(), fileTrailDto.getDispatchedOn() != null ? fileTrailDto.getDispatchedOn() : LocalDateTime.now()));
        fileTrailService.saveFileTrail(fileTrailDto);

        return "redirect:/files/view?id=" + fileId;
    }

}
