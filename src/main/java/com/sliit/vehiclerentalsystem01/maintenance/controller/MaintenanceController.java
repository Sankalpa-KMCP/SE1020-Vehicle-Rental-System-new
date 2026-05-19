package com.sliit.vehiclerentalsystem01.maintenance.controller;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.core.security.UserSessionKeys;
import com.sliit.vehiclerentalsystem01.maintenance.entity.MaintenanceRecord;
import com.sliit.vehiclerentalsystem01.maintenance.service.MaintenanceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final MaintenanceService maintenanceService;

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    private static boolean admin(HttpSession session) {
        boolean sessionExists = session != null;

        if (!sessionExists) {
            return false;
        }

        Object adminLoginAttribute = session.getAttribute(UserSessionKeys.ADMIN_LOGGED_IN);

        return Boolean.TRUE.equals(adminLoginAttribute);
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        model.addAttribute("maintenanceList", maintenanceService.findAll());
        model.addAttribute("maintenanceCount", maintenanceService.countInMaintenance());

        return "user/maintenance-dashboard";
    }

    @PostMapping("/add")
    public String addRecord(HttpSession session,
                            @RequestParam String vehicleId,
                            @RequestParam String vehicleType,
                            @RequestParam String vehicleModel,
                            @RequestParam String mileage,
                            @RequestParam(required = false) String lastServiceDate,
                            @RequestParam(required = false) String nextServiceDate,
                            @RequestParam String maintenanceReason,
                            @RequestParam(required = false) String notes,
                            @RequestParam(required = false) MultipartFile vehiclePhoto) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        MaintenanceRecord record = new MaintenanceRecord();

        record.setVehicleId(vehicleId);
        record.setVehicleType(vehicleType);
        record.setVehicleModel(vehicleModel);
        record.setMileage(mileage);
        record.setMaintenanceReason(maintenanceReason);

        boolean lastServiceDateWasProvided = lastServiceDate != null && !lastServiceDate.isBlank();

        if (lastServiceDateWasProvided) {
            LocalDate parsedLastServiceDate = LocalDate.parse(lastServiceDate);

            record.setLastServiceDate(parsedLastServiceDate);
        }

        boolean nextServiceDateWasProvided = nextServiceDate != null && !nextServiceDate.isBlank();

        if (nextServiceDateWasProvided) {
            LocalDate parsedNextServiceDate = LocalDate.parse(nextServiceDate);

            record.setNextServiceDate(parsedNextServiceDate);
        }

        record.setNotes(notes);

        try {
            String photoFileName = savePhoto(vehicleId, vehiclePhoto);

            record.setPhotoFileName(photoFileName);

            maintenanceService.addRecord(record);
        } catch (ValidationException ex) {
            return "redirect:/maintenance?error=" + ex.getMessage();
        } catch (IOException ex) {
            return "redirect:/maintenance?error=photo_upload_failed";
        }

        return "redirect:/maintenance";
    }

    @PostMapping("/delete")
    public String deleteRecord(HttpSession session,
                               @RequestParam String vehicleId) {
        boolean userIsAdmin = admin(session);

        if (!userIsAdmin) {
            return "redirect:/auth/staff-login";
        }

        maintenanceService.deleteRecord(vehicleId);

        return "redirect:/maintenance";
    }

    @GetMapping("/photos/{fileName}")
    public ResponseEntity<Resource> vehiclePhoto(@PathVariable String fileName) throws IOException {
        Path photoPath = photoDirectory().resolve(fileName).normalize();

        if (!photoPath.startsWith(photoDirectory()) || !Files.isRegularFile(photoPath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(photoPath);
        MediaType mediaType = mediaTypeFor(fileName);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    private String savePhoto(String vehicleId, MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) {
            throw new ValidationException("invalid_photo");
        }

        String originalFilename = photo.getOriginalFilename();
        String extension = extensionOf(originalFilename);

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new ValidationException("invalid_photo");
        }

        Path directory = photoDirectory();

        Files.createDirectories(directory);

        String safeVehicleId = vehicleId == null ? "vehicle" : vehicleId.trim().replaceAll("[^A-Za-z0-9_-]", "-");
        String fileName = safeVehicleId + "-" + System.currentTimeMillis() + "." + extension;
        Path destination = directory.resolve(fileName).normalize();

        if (!destination.startsWith(directory)) {
            throw new ValidationException("invalid_photo");
        }

        photo.transferTo(destination);

        return fileName;
    }

    private Path photoDirectory() {
        return Paths.get(dataDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve("maintenance-photos");
    }

    private static String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private static MediaType mediaTypeFor(String fileName) {
        String extension = extensionOf(fileName);

        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.parseMediaType("image/webp");
        };
    }
}
