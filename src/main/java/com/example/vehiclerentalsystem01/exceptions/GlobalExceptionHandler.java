package com.example.vehiclerentalsystem01.exceptions;

import com.example.vehiclerentalsystem01.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FileStorageService.FileStorageException.class)
    public String handleFileStorageException(FileStorageService.FileStorageException e, Model model) {
        logger.error("File storage error: {}", e.getMessage(), e);
        model.addAttribute("error", "System error occurred while accessing files. Please try again.");
        return "WEB-INF/jsp/error";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        logger.warn("Business error: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        return "WEB-INF/jsp/error";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException e, Model model) {
        logger.warn("Validation error: {}", e.getMessage());
        model.addAttribute("error", e.getMessage());
        model.addAttribute("errorType", "validation");
        return "WEB-INF/jsp/error/validation-error";
    }

    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException e,
                                               RedirectAttributes redirectAttributes) {
        logger.warn("Authentication error: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/login.jsp?error=auth";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unexpected error occurred", e);
        model.addAttribute("error", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorType", "system");
        model.addAttribute("details", e.getMessage());
        return "WEB-INF/jsp/error/generic-error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        logger.warn("Invalid argument: {}", e.getMessage());
        model.addAttribute("error", "Invalid input: " + e.getMessage());
        return "WEB-INF/jsp/error";
    }

    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(NumberFormatException e, Model model) {
        logger.warn("Number format error: {}", e.getMessage());
        model.addAttribute("error", "Invalid number format provided.");
        return "WEB-INF/jsp/error";
    }
}
