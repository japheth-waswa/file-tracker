package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.Setting;
import com.elijahwaswa.filetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettingRepository extends JpaRepository<Setting, String> {
    Setting findById(UUID id);
}

