package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.util.FileStatus;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;

import java.util.List;
import java.util.UUID;

//todo delete the fileTrail
public interface FileService {
    FileDto saveFile(FileDto fileDto,String idNumber,String fullNames,String department);
    FileDto updateFile(FileDto fileDto);
    FileDto fetchFile(UUID id) throws ResourceNotFoundException;
    FileDto fetchFile(String lrNo) throws ResourceNotFoundException;
    FileDto fetchFile(String lrNo,String currentUserIdNumber) throws ResourceNotFoundException;
    List<FileDto> fetchFiles(int pageNumber,int pageSize) throws ResourceNotFoundException;
    List<FileDto> fetchFiles(int pageNumber,int pageSize,String lrNo) throws ResourceNotFoundException;
    List<FileDto> fetchFiles(int pageNumber, int pageSize, FileStatus fileStatus) throws ResourceNotFoundException;
    List<FileDto> fetchFiles(String currentUserIdNumber,int pageNumber, int pageSize) throws ResourceNotFoundException;
    boolean deleteFile(UUID id);
    long count();
    long countByLrNo(String lrNo);
    long countByFileStatus(FileStatus fileStatus);
    long countByCurrentUserIdNumber(String currentUserIdNumber);
}
