package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByIdNumber(String idNumber);
}
