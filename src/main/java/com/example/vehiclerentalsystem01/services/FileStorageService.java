package com.example.vehiclerentalsystem01.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.storage.base-path:${user.home}/RentalData}")
    private String basePath;

    public void ensureDirectoryExists(String directory) {
        try {
            Path dirPath = Paths.get(basePath, directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Created directory: {}", dirPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", directory, e);
            throw new FileStorageException("Failed to create directory: " + directory, e);
        }
    }

    public boolean writeToFile(String filename, List<String> lines, boolean append) {
        try {
            ensureDirectoryExists("");
            Path filePath = Paths.get(basePath, filename);
            
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                    append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            logger.debug("Successfully wrote {} lines to file: {}", lines.size(), filename);
            return true;
        } catch (IOException e) {
            logger.error("Failed to write to file: {}", filename, e);
            throw new FileStorageException("Failed to write to file: " + filename, e);
        }
    }

    public List<String> readAllLines(String filename) {
        try {
            Path filePath = Paths.get(basePath, filename);
            if (!Files.exists(filePath)) {
                logger.warn("File does not exist: {}", filename);
                return new ArrayList<>();
            }
            
            List<String> lines = Files.readAllLines(filePath);
            logger.debug("Read {} lines from file: {}", lines.size(), filename);
            return lines;
        } catch (IOException e) {
            logger.error("Failed to read from file: {}", filename, e);
            throw new FileStorageException("Failed to read from file: " + filename, e);
        }
    }

    public boolean fileExists(String filename) {
        Path filePath = Paths.get(basePath, filename);
        return Files.exists(filePath);
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(basePath, filename);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                logger.info("Deleted file: {}", filename);
            }
            return deleted;
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", filename, e);
            throw new FileStorageException("Failed to delete file: " + filename, e);
        }
    }

    public String getBasePath() {
        return basePath;
    }

    public Path getFilePath(String filename) {
        return Paths.get(basePath, filename);
    }

    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message) {
            super(message);
        }

        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
