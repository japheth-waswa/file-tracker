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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Helpers {
    public static final String JSP_LOCATION_PREFIX = "/WEB-INF/jsp/";
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String RESET_PASSWORD_URL = "/reset-password";
    public static final String NEW_PASSWORD_URL = "/new-password";
    public static final String AUTHENTICATED_ROOT_URL = "/dashboard";
    public static final String TWO_FACTOR_AUTHENTICATION_URL = "/2fa";
    public static final String TWO_FACTOR_BOOL_FLAG = "validTwoFactor";
    public static final String TWO_FACTOR_AUTHENTICATION_TOTP_URL = "/2fa-totp";
    public static final List<String> PUBLIC_PATHS = List.of("/css/**", "/js/**", "/images/**", "/fonts/**", "/plugins/**", "/webjars/**",LOGIN_URL,RESET_PASSWORD_URL,NEW_PASSWORD_URL);
    public static final String ROLE_PREPEND = "ROLE_";
    public static final String SU_ADMIN_ID = "1234";
    public static final String USER_DEFAULT_PASSWORD = "1234";
    public static final String LOGIN_PAGE_VIEW = "auth/login";
    public static final String RESET_PASSWORD_PAGE_VIEW = "auth/reset-password";
    public static final String NEW_PASSWORD_PAGE_VIEW = "auth/new-password";
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int RESET_PASSWORD_EXPIRY_MINUTES = 60*24;//24 hrs
    public static final String DEFAULT_DEPARTMENT_NAME = "storage";

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

    public static String generateBaseUrl(HttpServletRequest request){
        return request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
    }

    public static Set<UserRole> parseAuthenticatedRoles(Authentication authentication){
        Set<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return roles.stream().filter(role->role.contains(ROLE_PREPEND)).map(role -> UserRole.valueOf(role.replace(ROLE_PREPEND,""))).collect(Collectors.toSet());
    }

    public static Set<UserRight> parseAuthenticatedRights(Authentication authentication){
        Set<String> rights = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return rights.stream().filter(right->!right.contains(ROLE_PREPEND)).map(UserRight::valueOf).collect(Collectors.toSet());
    }

}
