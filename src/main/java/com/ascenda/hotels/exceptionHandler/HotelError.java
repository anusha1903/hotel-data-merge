package com.ascenda.hotels.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotelError {
    private int status;
    private String message;
    private String error;
}
