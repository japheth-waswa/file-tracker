package com.elijahwaswa.filetracker.service;

import com.elijahwaswa.filetracker.dto.FileDto;
import com.elijahwaswa.filetracker.model.File;
import com.elijahwaswa.filetracker.repository.FileRepository;
import com.elijahwaswa.filetracker.service.file.FileService;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class FileFakerService {
    private FileService fileService;
    private final Logger LOGGER = LoggerFactory.getLogger(FileFakerService.class);

    @Transactional
    public void generateFakeFiles() {
        int numThreads = 10;//number of threads to use
        int numRecordsPerThread = 100_0000 / numThreads;//number of records to generate per thread

        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {

            IntStream.range(0, numThreads).forEach(i -> {
                executor.submit(() -> {
                    Faker faker = new Faker();

                    IntStream.range(0, numRecordsPerThread).forEach(j -> {
                        try {
                            FileDto fileDto = new FileDto();
                            fileDto.setLrNo(new Random().nextLong(500_000, 10_000_000_000L) + "-" + faker.medical().diseaseName() + "-" + faker.food().dish());
                            fileService.saveFile(fileDto, "" + faker.number().numberBetween(500_000, 20_0000_000), faker.name().fullName(), faker.job().title());
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage());
                        }
                    });

                });
            });

            executor.shutdown();
            while (!executor.isTerminated()) {
                //wait for all threads to finish
            }
        }
    }
}
