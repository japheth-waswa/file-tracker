package com.elijahwaswa.filetracker.config;

import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class InitExecutor implements CommandLineRunner {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private final String SU_ROLES = Arrays.stream(UserRole.values()).map(UserRole::name).collect(Collectors.joining(","));
    private final String SU_RIGHTS = Arrays.stream(UserRight.values()).map(UserRight::name).collect(Collectors.joining(","));

    @Override
    public void run(String... args) throws Exception {
        // check if super admin exists in db, if not,then create.
        String SU_ADMIN_ID = Helpers.SU_ADMIN_ID;
        User adminUser = userRepository.findByIdNumber(SU_ADMIN_ID);
        if (adminUser == null) {
            User user = new User();
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setIdNumber(SU_ADMIN_ID);
            user.setPassword(passwordEncoder.encode(Helpers.USER_DEFAULT_PASSWORD));
            user.setRoles(SU_ROLES);
            user.setRights(SU_RIGHTS);
            user.setDepartment("Super Admin");
            user.setFirstName("Jeff");
            user.setMiddleName("Elijah");
            user.setOtherNames("Waswa");
            user.setEmail("japhethwaswa@gmail.com");
            userRepository.save(user);
        }
    }
}
