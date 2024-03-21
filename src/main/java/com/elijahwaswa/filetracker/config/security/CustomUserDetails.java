package com.elijahwaswa.filetracker.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User implements UserDetails {
    private final  String department;
    private final String fullNames;
    private final String idNumber;
    @Setter
    private String twoFactorCode;

    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, com.elijahwaswa.filetracker.model.User user) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.department = user.getDepartment();
        this.idNumber = user.getIdNumber();
        this.fullNames= String.format("%s %s %s", user.getFirstName(), user.getMiddleName(), user.getOtherNames());
    }

}
