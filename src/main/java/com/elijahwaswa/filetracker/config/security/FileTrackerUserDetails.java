package com.elijahwaswa.filetracker.config.security;

import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.Helpers;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class FileTrackerUserDetails implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByIdNumber(username);
        if (user == null) throw new UsernameNotFoundException("User not found: " + username);

        String password = user.getPassword();
        List<GrantedAuthority> authorities = new ArrayList<>();
        String roles = user.getRoles();
        String rights = user.getRights();
        if (roles != null && !roles.isEmpty()) {
            //split where , is found
            Arrays.stream(roles.split(",")).sequential().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(Helpers.ROLE_PREPEND + role));
            });
        }
        if (rights != null && !rights.isEmpty()) {
            //split where , is found
            Arrays.stream(rights.split(",")).sequential().forEach(right -> {
                authorities.add(new SimpleGrantedAuthority(right));
            });
        }
        return new CustomUserDetails(username, password,  user.getAccountStatus() == AccountStatus.ACTIVE, true, true, true, authorities, user);
    }
}
