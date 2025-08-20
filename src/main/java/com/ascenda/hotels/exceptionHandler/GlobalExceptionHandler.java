package com.ascenda.hotels.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Hotel Not found exception
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(HotelNotFoundException.class)
    public ResponseEntity<HotelError> handleHotelNotFound(HotelNotFoundException ex, WebRequest request) {
        HotelError error = new HotelError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIngestionException.class)
    public ResponseEntity<HotelError> handleDataIngestionException(DataIngestionException ex, WebRequest request) {
        HotelError error = new HotelError(
                HttpStatus.EXPECTATION_FAILED.value(),
                HttpStatus.EXPECTATION_FAILED.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<HotelError> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        HotelError error = new HotelError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HotelError> handleInvalidJson(HttpMessageNotReadableException ex) {
        HotelError error = new HotelError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Avoid requesting for blank attribute. Malformed JSON or invalid field types: %s".formatted(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handle Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HotelError> handleAll(Exception ex, WebRequest request) {
        logger.error("Unhandled exception occurred", ex);
        HotelError error = new HotelError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
