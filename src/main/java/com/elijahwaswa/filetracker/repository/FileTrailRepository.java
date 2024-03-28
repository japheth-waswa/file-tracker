package com.elijahwaswa.filetracker.repository;

import com.elijahwaswa.filetracker.model.File;
import com.elijahwaswa.filetracker.model.FileTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileTrailRepository extends JpaRepository<FileTrail, String> {
    FileTrail findById(UUID id);
    Page<FileTrail> findAllByLrNo(String lrNo, Pageable pageable);
    long countByLrNo(String lrNo);
}
