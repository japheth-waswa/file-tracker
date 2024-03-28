package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileTrailDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;

import java.util.List;

public interface FileTrailService {
    FileTrailDto saveFileTrail(FileTrailDto fileTrailDto);
    FileTrailDto updateFileTrail(FileTrailDto fileTrailDto);

    List<FileTrailDto> fetchFileTrails(int pageNumber, int pageSize, String lrNo) throws ResourceNotFoundException;
    long countByLrNo(String lrNo);

    FileTrailDto computeDueDate(FileTrailDto fileTrailDto);
}
