package com.ascenda.hotels.exceptionHandler;

/**
 * This exception shoyld be thrown if there is no hotel entry in dB either for requested hotel_id or for requested destination_id
 */
public class HotelNotFoundException extends RuntimeException{
    public HotelNotFoundException(String message) {
        super(message);
    }
}
