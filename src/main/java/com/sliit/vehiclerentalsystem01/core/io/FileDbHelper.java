package com.sliit.vehiclerentalsystem01.core.io;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileDbHelper {

    private static final Logger log = LoggerFactory.getLogger(FileDbHelper.class);

    /**
     * Atomically writes lines to a target file.
     * It writes to a temporary file in the same directory and then moves it using ATOMIC_MOVE.
     */
    public static void writeAtomic(Path targetPath, List<String> lines) {
        if (targetPath == null || lines == null) {
            throw new IllegalArgumentException("Path and lines must not be null");
        }
        Path parent = targetPath.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new StorageException("Failed to create parent directories for " + targetPath, e);
            }
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(parent, targetPath.getFileName().toString(), ".tmp");
            Files.write(tempFile, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tempFile, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ex) {
                    log.warn("Failed to delete temp file {}", tempFile, ex);
                }
            }
            throw new StorageException("Failed to write to file atomically: " + targetPath, e);
        }
    }
}
