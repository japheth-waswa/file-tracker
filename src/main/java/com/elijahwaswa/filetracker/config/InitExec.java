package com.elijahwaswa.filetracker.config;

import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.repository.UserRepository;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class InitExec implements CommandLineRunner {

private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private final String SU_ADMIN_ROLE = String.join(",", List.of(UserRole.SU.name()));

    @Override
    public void run(String... args) throws Exception {
        // check if super admin exists in db, if not,then create.
        String SU_ADMIN_ID = "1234";
        User adminUser = userRepository.findByIdNumber(SU_ADMIN_ID);
        if (adminUser == null) {
            String ADMIN_PASSWORD = "4321";
            User user = new User();
            user.setIdNumber(SU_ADMIN_ID);
            user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            user.setRoles(SU_ADMIN_ROLE);
            user.setDepartment("My custom department");
            userRepository.save(user);
        }

        createTestUsers();//todo remove this after testing user access to various resources.
    }

    private void createTestUsers(){
        //admin
        String admin = "carol";
        User adminUser = userRepository.findByIdNumber(admin);
        if (adminUser == null) {
            User user = new User();
            user.setIdNumber(admin);
            user.setPassword(passwordEncoder.encode("carol1234"));
            user.setRoles(UserRole.ADMIN.name());
//            user.setDepartment("My custom department");
            userRepository.save(user);
        }

        //user with supervisor right
        String user1 = "nagel";
        User user_1 = userRepository.findByIdNumber(user1);
        if (user_1 == null) {
            User user = new User();
            user.setIdNumber(user1);
            user.setPassword(passwordEncoder.encode("nagel1234"));
            user.setRoles(UserRole.USER.name());
            user.setRights(UserRight.SUPERVISOR.name());
            user.setDepartment("registration");
            userRepository.save(user);
        }

        //user without supervisor right
        String user2 = "royce";
        User user_2 = userRepository.findByIdNumber(user2);
        if (user_2 == null) {
            User user = new User();
            user.setIdNumber(user2);
            user.setPassword(passwordEncoder.encode("royce1234"));
            user.setRoles(UserRole.USER.name());
//            user.setRights(UserRight.SUPERVISOR.name());
            user.setDepartment("land administration");
            userRepository.save(user);
        }

    }
}
