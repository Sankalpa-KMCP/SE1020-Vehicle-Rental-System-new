package com.sliit.vehiclerentalsystem01.core.service;

import com.sliit.vehiclerentalsystem01.core.exceptions.StorageException;
import com.sliit.vehiclerentalsystem01.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public String saveAvatar(MultipartFile file, String userId) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path avatarDir = Paths.get(dataDirectory).toAbsolutePath().normalize().resolve("avatars");
            Files.createDirectories(avatarDir);

            String extension = resolveExtension(file.getOriginalFilename());
            String safeUserId = sanitizeUserId(userId);
            String filename = safeUserId + "_" + UUID.randomUUID() + extension;

            Path target = avatarDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new StorageException("Failed to save avatar image", e);
        }
    }

    public void attachAvatar(User user, MultipartFile file) {
        if (user == null) {
            return;
        }
        String filename = saveAvatar(file, user.getId());
        if (filename != null) {
            user.setProfilePicture(filename);
        }
    }

    private static String resolveExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        if (ALLOWED_EXTENSIONS.contains(ext)) {
            return ext;
        }
        return ".jpg";
    }

    private static String sanitizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return "user";
        }
        return userId.trim().replaceAll("[^a-zA-Z0-9\\-]", "_");
    }
}
