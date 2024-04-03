package com.elijahwaswa.filetracker.util;

import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Helpers {
    public static final String JSP_LOCATION_PREFIX = "/WEB-INF/jsp/";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String RESET_PASSWORD_URL = "/reset-password";
    public static final String NEW_PASSWORD_URL = "/new-password";
    public static final String AUTHENTICATED_ROOT_URL = "/files";
    public static final String TWO_FACTOR_AUTHENTICATION_URL = "/2fa";
    public static final String TWO_FACTOR_BOOL_FLAG = "validTwoFactor";
    public static final String TWO_FACTOR_AUTHENTICATION_TOTP_URL = "/2fa-totp";
    public static final List<String> PUBLIC_PATHS = List.of("/css/**", "/js/**", "/images/**", "/fonts/**", "/plugins/**", "/webjars/**", LOGIN_URL, RESET_PASSWORD_URL, NEW_PASSWORD_URL);
    public static final String ROLE_PREPEND = "ROLE_";
    public static final String SU_ADMIN_ID = "1234";
    public static final String USER_DEFAULT_PASSWORD = "1234";
    public static final String LOGIN_PAGE_VIEW = "auth/login";
    public static final String RESET_PASSWORD_PAGE_VIEW = "auth/reset-password";
    public static final String NEW_PASSWORD_PAGE_VIEW = "auth/new-password";
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int RESET_PASSWORD_EXPIRY_MINUTES = 60 * 24;//24 hrs
    public static final String DEFAULT_DEPARTMENT_NAME = "storage";
    public static final String DATE_TIME_VIEW_FORMAT = "dd/MM/yyyy HH:mm:ss a";
    public static final int REMINDER_DURATION = 7;
    public static final DurationType DURATION_TYPE = DurationType.DAYS;


    private Helpers() {
    }

    public static Pageable buildPageable(int pageNumber, int pageSize) {
        return buildPageable(pageNumber, pageSize, new ArrayList<>());
    }

    public static Pageable buildPageable(int pageNumber, int pageSize, List<Sort.Order> orders) {
        if (!orders.isEmpty()) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    public static <T, U> List<T> parsePageableRecordsToList(Class<T> clazz, Page<U> records, String nullMessage) {
        if (records.isEmpty()) throw new ResourceNotFoundException(nullMessage);

        ModelMapper modelMapper = new ModelMapper();
        return records
                .stream()
                .map(record -> modelMapper.map(record, clazz))
                .toList();
    }

    public static String loadJspIntoString(
            HttpServletRequest request,
            HttpServletResponse response,
            String jspPath,
            Map<String, Object> variables
    ) {
        try {
            if (!variables.isEmpty()) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
            }

            CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
            RequestDispatcher dispatcher = request.getRequestDispatcher(JSP_LOCATION_PREFIX + jspPath);
            dispatcher.include(request, responseWrapper);

            return responseWrapper.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
    }

    public static Set<UserRole> parseAuthenticatedRoles(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return roles.stream().filter(role -> role.contains(ROLE_PREPEND)).map(role -> UserRole.valueOf(role.replace(ROLE_PREPEND, ""))).collect(Collectors.toSet());
    }

    public static Set<UserRight> parseAuthenticatedRights(Authentication authentication) {
        Set<String> rights = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return rights.stream().filter(right -> !right.contains(ROLE_PREPEND)).map(UserRight::valueOf).collect(Collectors.toSet());
    }

    public static void setViewModelAttrs(Authentication authentication,Model model){
        if(authentication == null)return;

        Set<UserRole> roles = parseAuthenticatedRoles(authentication);
        if(roles.contains(UserRole.SU))model.addAttribute("isSu",true);
        if(roles.contains(UserRole.ADMIN))model.addAttribute("isAdmin",true);
        if(roles.contains(UserRole.USER))model.addAttribute("isUser",true);
    }

    public static long timeDifference(ChronoUnit chronoUnit, LocalDateTime from, LocalDateTime to) {
        return chronoUnit.between(from, to);
    }

    public static String formatDateTime(LocalDateTime dateTime, String formatPattern) {
        try {
            return dateTime.format(DateTimeFormatter.ofPattern(formatPattern));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String appendSchar(long longParam, String strParam) {
        if (longParam == 1) return strParam;
        return strParam + "s";
    }

    public static String formatDuration(long durationInSeconds) {
        Duration duration = Duration.ofSeconds(durationInSeconds);
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(durationInSeconds, 0, ZoneOffset.UTC);
        LocalDateTime epoch = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
        Period period = Period.between(epoch.toLocalDate(), dateTime.toLocalDate());

        long years = period.getYears();
        long months = period.getMonths();
        long days = period.getDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder stringBuilder = new StringBuilder();
        if (years > 0) stringBuilder.append(years).append(appendSchar(years, "year")).append(" ");
        if (months > 0) stringBuilder.append(months).append(appendSchar(months, "month")).append(" ");
        if (days > 0) stringBuilder.append(days).append(appendSchar(days, "day")).append(" ");
        if (hours > 0) stringBuilder.append(hours).append(appendSchar(hours, "hr")).append(" ");
        if (minutes > 0) stringBuilder.append(minutes).append(appendSchar(minutes, "min")).append(" ");
        if (seconds > 0) stringBuilder.append(seconds).append(appendSchar(seconds, "sec")).append(" ");

        return stringBuilder.toString().trim();
    }

    public static LocalDateTime computeDueDateUsingSetting(LocalDateTime dateTime, long reminderDuration, DurationType durationType) {
        boolean parseEndOfDay = false;
        LocalDateTime dueDate = switch (durationType) {
            case MONTHS -> {
                parseEndOfDay = true;
                yield dateTime.plusMonths(reminderDuration);
            }
            case DAYS -> {
                parseEndOfDay = true;
                yield dateTime.plusDays(reminderDuration);
            }
            case HOURS -> dateTime.plusHours(reminderDuration);
            case MINUTES -> dateTime.plusMinutes(reminderDuration);
        };

        if (parseEndOfDay) {
            //make it end of day
            return dueDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        } else {
            return dueDate;
        }
    }

    public static String getLoggedInUsername(){
        String username = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) username = userDetails.getUsername();
        return username;
    }
}
