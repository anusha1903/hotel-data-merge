package com.ascenda.hotels.exceptionHandler;

public class HotelNotFoundException extends RuntimeException{
    public HotelNotFoundException(String message) {
        super(message);
    }
}
