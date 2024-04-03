package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.File;
import com.elijahwaswa.filetracker.model.Notification;
import com.elijahwaswa.filetracker.util.FileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByIdNumber(String idNumber, Pageable pageable);
    long countByIdNumber(String idNumber);
}
