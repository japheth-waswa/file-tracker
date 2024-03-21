package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.File;
import com.elijahwaswa.filetracker.model.User;
import com.elijahwaswa.filetracker.util.AccountStatus;
import com.elijahwaswa.filetracker.util.FileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, String> {
    File findById(UUID id);
    File findByLrNo(String lrNo);
    void deleteById(UUID id);
    Page<File> findAllByLrNo(String lrNo, Pageable pageable);
    Page<File> findAllByFileStatus(FileStatus fileStatus, Pageable pageable);
    long countByLrNo(String lrNo);
    long countByFileStatus(FileStatus fileStatus);
}
