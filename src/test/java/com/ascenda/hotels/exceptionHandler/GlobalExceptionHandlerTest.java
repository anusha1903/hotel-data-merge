package com.ascenda.hotels.exceptionHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GlobalExceptionHandler} class.
 * Tests exception handling and error response formatting.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    /**
     * Validates proper handling of HotelNotFoundException.
     */
    @Test
    void testHandleHotelNotFoundException() {
        String errorMessage = "Hotel not found with ID: hotel123";
        HotelNotFoundException exception = new HotelNotFoundException(errorMessage);

        ResponseEntity<HotelError> response = globalExceptionHandler.handleHotelNotFound(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Validates proper handling of DataIngestionException.
     */
    @Test
    void testHandleDataIngestionException() {
        String errorMessage = "Failed to ingest data from external source";
        DataIngestionException exception = new DataIngestionException(errorMessage);

        ResponseEntity<HotelError> response = globalExceptionHandler.handleDataIngestionException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Validates that IllegalArgumentException is handled by the generic handler since handleBadRequest is private.
     */
    @Test
    void testHandleIllegalArgumentExceptionThroughGenericHandler() {
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<HotelError> response = globalExceptionHandler.handleAll(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    /**
     * Validates proper handling of generic exceptions.
     */
    @Test
    void testHandleGenericException() {
        String errorMessage = "An unexpected error occurred";
        Exception exception = new RuntimeException(errorMessage);

        ResponseEntity<HotelError> response = globalExceptionHandler.handleAll(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}