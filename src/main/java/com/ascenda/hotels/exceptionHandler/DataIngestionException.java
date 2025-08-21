package com.ascenda.hotels.exceptionHandler;

/**
 * This exveption should be thrown when there is failure in
 * hotel data reading from sources, parsing, merging and saving data to dB
 */
public class DataIngestionException extends RuntimeException {
    public DataIngestionException(String message) {
        super(message);
    }
}
