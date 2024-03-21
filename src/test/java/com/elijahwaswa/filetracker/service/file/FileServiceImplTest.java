package com.elijahwaswa.filetracker.service.file;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.exception.ex.InternalException;
import com.elijahwaswa.filetracker.exception.ex.ResourceNotFoundException;
import com.elijahwaswa.filetracker.util.FileNature;
import com.elijahwaswa.filetracker.util.FileStatus;
import com.elijahwaswa.filetracker.util.UserRight;
import com.elijahwaswa.filetracker.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceImplTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FileService fileService;
    private FileDto file1, file2, file3;

    private void truncateH2DB() {
        //truncate or delete data from all tables
        String[] tables = {"files"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table;
            jdbcTemplate.execute(sql);
        }
    }

    @BeforeEach
    void setUp() {
        truncateH2DB();
        file1 = new FileDto();
        file1.setLrNo("LR/1/1");
        file1.setCfNo("CF/1/1");
        file1.setIrNo("IR/1/1");
        file1.setAreaSize(0.0089);
        file1.setFileNature(FileNature.LEASEHOLD);
        file1.setFileStatus(FileStatus.TRANSACTABLE);

        file2 = new FileDto();
        file2.setLrNo("LR/2/2");
        file2.setIrNo("IR/2/2");
        file2.setAreaSize(0.0089);
        file2.setFileNature(FileNature.ABSOLUTE);
        file2.setFileStatus(FileStatus.LOCKED);

        file3 = new FileDto();
        file3.setLrNo("LR/3/3");
        file3.setCfNo("CF/3/3");
        file3.setFileNature(FileNature.SECTIONAL);
        file3.setFileStatus(FileStatus.TRANSACTABLE);
    }

    @Test
    void saveFile() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertEquals(file2.getLrNo().toLowerCase(), savedFile2.getLrNo());
    }

    @Test
    void updateFile_success() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        //update
        savedFile3.setAreaSize(45.997);
        FileDto updatedFile = fileService.updateFile(savedFile3, "4321", "Jane Doe", "Registrar",
                List.of(UserRole.SU, UserRole.ADMIN),
                null);
        assertEquals(savedFile3.getAreaSize(), updatedFile.getAreaSize());
    }

    @Test
    void updateFile_throws() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        //update
        savedFile2.setLrNo("lr.no 209/1235");
        assertThrows(InternalException.class, () -> fileService.updateFile(savedFile2, "4321", "Jane Doe", "Registrar",
                List.of(UserRole.USER), null));
    }

    @Test
    void fetchFile_success() {
        FileDto savedFile1 = fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        FileDto fetchedFile = fileService.fetchFile(savedFile2.getId());
        assertEquals(file2.getLrNo().toLowerCase(), fetchedFile.getLrNo());
    }

    @Test
    void fetchFile_failed() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertThrows(ResourceNotFoundException.class, () -> fileService.fetchFile(UUID.randomUUID()));
    }

    @Test
    void fetchFile_lrNo_success() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        FileDto fetchedFile = fileService.fetchFile(file1.getLrNo());
        assertEquals(file1.getLrNo().toLowerCase(), fetchedFile.getLrNo());
    }

    @Test
    void fetchFile_lrNo_failed() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertThrows(ResourceNotFoundException.class, () -> fileService.fetchFile("lr.no 12715/609"));
    }

    @Test
    void fetchFiles_success() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        List<FileDto> files = fileService.fetchFiles(0, 10);
        assertEquals(3, files.size());
        assertEquals(file3.getLrNo().toLowerCase(), files.getFirst().getLrNo());
    }

    @Test
    void fetchFiles_failed() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertThrows(ResourceNotFoundException.class, () -> fileService.fetchFiles(2, 10));
    }

    @Test
    void fetchFiles_lrNo_success() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        List<FileDto> files = fileService.fetchFiles(0, 10, file2.getLrNo());
        assertEquals(1, files.size());
        assertEquals(file2.getLrNo().toLowerCase(), files.getFirst().getLrNo());
    }

    @Test
    void fetchFiles_lrNo_error() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertThrows(ResourceNotFoundException.class, () -> fileService.fetchFiles(2, 10, "block 12/900"));
    }

    @Test
    void fetchFiles_fileStatus_success() {
        fileService.saveFile(file1, "1234", "John Doe", "Lands");
        fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        List<FileDto> files = fileService.fetchFiles(0, 10, file1.getFileStatus());
        assertEquals(2, files.size());
        assertEquals(file1.getLrNo().toLowerCase(), files.get(1).getLrNo());
    }

    @Test
    void deleteFile() {
        FileDto savedFile1 = fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        fileService.deleteFile(savedFile2.getId());
        List<FileDto> files = fileService.fetchFiles(0, 10);
        assertEquals(2, files.size());
        assertEquals(file3.getLrNo().toLowerCase(), files.getFirst().getLrNo());
    }

    @Test
    void count() {
        FileDto savedFile1 = fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertEquals(3, fileService.count());
    }

    @Test
    void countByLrNo() {
        FileDto savedFile1 = fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertEquals(1, fileService.countByLrNo(file1.getLrNo()));
        assertEquals(0,  fileService.countByLrNo("delivery"));
    }

    @Test
    void countByFileStatus() {
        assertEquals(0,fileService.countByFileStatus(FileStatus.TRANSACTABLE));
        FileDto savedFile1 = fileService.saveFile(file1, "1234", "John Doe", "Lands");
        FileDto savedFile2 = fileService.saveFile(file2, "4321", "Jane Doe", "Registrar");
        FileDto savedFile3 = fileService.saveFile(file3, "0987", "Peter Pan", "Valuation");
        assertEquals(1, fileService.countByFileStatus(FileStatus.LOCKED));
        assertEquals(2, fileService.countByFileStatus(FileStatus.TRANSACTABLE));
    }
}