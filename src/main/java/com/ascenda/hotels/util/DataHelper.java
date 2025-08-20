package com.ascenda.hotels.util;

import com.ascenda.hotels.constants.Constants;
import com.ascenda.hotels.exceptionHandler.HotelNotFoundException;
import com.ascenda.hotels.model.request.HotelRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.regex.Pattern;

public class DataHelper {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");


    public static boolean validateHotel(JsonNode hotelNode){
        boolean result = true;
        String hotelId = hotelNode.get("Id").asText();
        String destinationId = hotelNode.get("destination_id").asText();
        String hotelName = hotelNode.get("name").asText();
        JsonNode location = hotelNode.get("location");
        String address =  location.get("address").asText();
        if(isNullOrEmpty(hotelId) || isNullOrEmpty(destinationId) || isNullOrEmpty(hotelName) || isNullOrEmpty(address)){
            result = false;
        }
        return result;
    }

    // check for null, removed whitespaces and check for empty
    public static boolean isNullOrEmpty(String str) {
        if(str == null) { //checks if the string is null
            return true;
        } else if(str.trim().isEmpty()) { //checks if the string length uuis 0 i.e., empty string with no spaces
            return true;
        } else {
            String cleaned = WHITESPACE_PATTERN.matcher(str.trim()).replaceAll("");
            if(cleaned.isEmpty()) {
                return true;
            } else  {
                return false;
            }
        }
    }

    public static boolean validateUserRequest(HotelRequest hotelRequest){
        if (hotelRequest == null) {
            throw new IllegalArgumentException("Hotel request cannot be null");
        }
        
        boolean hasValidId = hotelRequest.getId() != null && !hotelRequest.getId().trim().isEmpty();
        boolean hasValidDestinationId = hotelRequest.getDestinationId() != null && hotelRequest.getDestinationId() > 0;
        
        if (!hasValidId && !hasValidDestinationId) {
            throw new IllegalArgumentException("Either hotel ID or destination ID must be provided and valid");
        }
        
        return hasValidId || hasValidDestinationId;
    }

}
