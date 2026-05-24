package com.sliit.vehiclerentalsystem01.core.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException ex, Model model) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Validation Failed");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("status", 400);
        model.addAttribute("pageTitle", "Validation Error | DriveAway");
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        ApiError error = new ApiError(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors
        );
        log.warn("Bean validation failed: {}", fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStorageException(StorageException ex, Model model) {
        log.error("Storage error occurred: ", ex);
        model.addAttribute("errorTitle", "Storage Database Error");
        model.addAttribute("errorMessage", "An error occurred with our file-based persistent storage database. Please contact support.");
        model.addAttribute("status", 500);
        model.addAttribute("pageTitle", "Database Error | DriveAway");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("An unexpected error occurred: ", ex);
        model.addAttribute("errorTitle", "Unexpected Server Error");
        model.addAttribute("errorMessage", "Something went wrong on our end. Please try again later.");
        model.addAttribute("status", 500);
        model.addAttribute("pageTitle", "Server Error | DriveAway");
        return "error";
    }
}
