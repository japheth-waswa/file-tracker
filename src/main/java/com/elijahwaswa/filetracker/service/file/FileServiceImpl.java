package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.exception.ex.InternalException;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.model.File;
import com.elijahwaswa.filetracker.repository.FileRepository;
import com.elijahwaswa.filetracker.util.FileStatus;
import com.elijahwaswa.filetracker.util.Helpers;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    //todo store audit trail
    private ModelMapper modelMapper;
    private FileRepository fileRepository;

    @Override
    public FileDto saveFile(FileDto fileDto, String idNumber, String fullNames, String department) {
        fileDto.setCurrentUserIdNumber(idNumber);
        fileDto.setCurrentUserFullNames(fullNames);
        fileDto.setCurrentDepartment(department);
        fileDto.setCreatedBy(fullNames);

        File savedFile = fileRepository.save(modelMapper.map(fileDto, File.class));
        return modelMapper.map(savedFile, FileDto.class);
    }

    @Override
    public FileDto updateFile(FileDto fileDto, String idNumber, String fullNames, String department, List<UserRole> userRoles, List<UserRight> userRights) {
        //fetch file
        File file = fileRepository.findById(fileDto.getId());
        if (file == null) throw new ResourceNotFoundException("File with id " + fileDto.getId() + " not found");

//        userRoles = userRoles == null ? List.of(UserRole.USER) : userRoles;
//        userRights = userRights == null ? List.of(UserRight.NORMAL) : userRights;
//
//        //ensure that only supervisor > and above are allowed to update the lrNo
//        if (fileDto.getLrNo() != null && !file.getLrNo().equalsIgnoreCase(fileDto.getLrNo())
//                && (!userRoles.contains(UserRole.SU)
//                && !userRoles.contains(UserRole.ADMIN)
//                && !(userRoles.contains(UserRole.USER) && userRights.contains(UserRight.SUPERVISOR)))) {
//            throw new InternalException("The current user is not allowed to update the lrNo");
//        }
        //skip null values
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(fileDto, file);
        //update
        File updatedFile = fileRepository.save(file);
        return modelMapper.map(updatedFile, FileDto.class);
    }

    @Override
    public FileDto fetchFile(UUID id) throws ResourceNotFoundException {
        File file = fileRepository.findById(id);
        if (file == null) throw new ResourceNotFoundException("File with id " + id + " not found");
        return modelMapper.map(file, FileDto.class);
    }

    @Override
    public FileDto fetchFile(String lrNo) throws ResourceNotFoundException {
        File file = fileRepository.findByLrNo(lrNo.toLowerCase());
        if (file == null) throw new ResourceNotFoundException("File with lrNo " + lrNo + " not found");
        return modelMapper.map(file, FileDto.class);
    }

    @Override
    public List<FileDto> fetchFiles(int pageNumber, int pageSize) throws ResourceNotFoundException {
        Page<File> files = fileRepository.findAll(Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(FileDto.class, files, "No files found!");
    }

    @Override
    public List<FileDto> fetchFiles(int pageNumber, int pageSize, String lrNo) throws ResourceNotFoundException {
        Page<File> files = fileRepository.findAllByLrNo(lrNo.toLowerCase(), Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(FileDto.class, files, "No files found!");
    }

    @Override
    public List<FileDto> fetchFiles(int pageNumber, int pageSize, FileStatus fileStatus) throws ResourceNotFoundException {
        Page<File> files = fileRepository.findAllByFileStatus(fileStatus, Helpers.buildPageable(pageNumber, pageSize, List.of(new Sort.Order(Sort.Direction.DESC, "createdAt"))));
        return Helpers.parsePageableRecordsToList(FileDto.class, files, "No files found!");
    }

    @Override
    @Transactional
    public boolean deleteFile(UUID id) {
        //todo ensure only the super admin can delete files
        fileRepository.deleteById(id);
        return true;
    }

    @Override
    public long count() {
        return fileRepository.count();
    }

    @Override
    public long countByLrNo(String lrNo) {
        return fileRepository.countByLrNo(lrNo.toLowerCase());
    }

    @Override
    public long countByFileStatus(FileStatus fileStatus) {
        return fileRepository.countByFileStatus(fileStatus);
    }
}
