package com.sliit.vehiclerentalsystem01.core.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleValidationException() {
        Model model = new ConcurrentModel();
        ValidationException ex = new ValidationException("Invalid email format");

        String view = handler.handleValidationException(ex, model);

        assertEquals("error", view);
        assertEquals("Validation Failed", model.getAttribute("errorTitle"));
        assertEquals("Invalid email format", model.getAttribute("errorMessage"));
        assertEquals(400, model.getAttribute("status"));
    }

    @Test
    void testHandleStorageException() {
        Model model = new ConcurrentModel();
        StorageException ex = new StorageException("IO failure", new java.io.IOException());

        String view = handler.handleStorageException(ex, model);

        assertEquals("error", view);
        assertEquals("Storage Database Error", model.getAttribute("errorTitle"));
        assertEquals(500, model.getAttribute("status"));
    }

    @Test
    void testHandleGenericException() {
        Model model = new ConcurrentModel();
        Exception ex = new Exception("Out of memory");

        String view = handler.handleGenericException(ex, model);

        assertEquals("error", view);
        assertEquals("Unexpected Server Error", model.getAttribute("errorTitle"));
        assertEquals(500, model.getAttribute("status"));
    }
}
