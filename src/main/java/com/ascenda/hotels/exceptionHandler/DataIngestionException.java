package com.ascenda.hotels.exceptionHandler;

public class DataIngestionException extends RuntimeException {
    public DataIngestionException(String message) {
        super(message);
    }
}
