package com.ascenda.hotels.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class DataHelperTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }


    @Test
    void testIsNullOrEmpty_WithNull() {
        assertTrue(DataHelper.isNullOrEmpty(null));
    }


    @Test
    void testIsNullOrEmpty_WithEmptyString() {
        assertTrue(DataHelper.isNullOrEmpty(""));
    }


    @Test
    void testIsNullOrEmpty_WithWhitespaceOnly() {
        assertTrue(DataHelper.isNullOrEmpty("   "));
        assertTrue(DataHelper.isNullOrEmpty("\t\n\r"));
        assertTrue(DataHelper.isNullOrEmpty(" \t \n "));
    }


    @Test
    void testIsNullOrEmpty_WithValidString() {
        assertFalse(DataHelper.isNullOrEmpty("Hello World"));
        assertFalse(DataHelper.isNullOrEmpty("Test"));
        assertFalse(DataHelper.isNullOrEmpty("123"));
    }


    @Test
    void testIsNullOrEmpty_WithMixedContent() {
        assertFalse(DataHelper.isNullOrEmpty("  Hello  "));
        assertFalse(DataHelper.isNullOrEmpty("\tTest\n"));
        assertFalse(DataHelper.isNullOrEmpty(" 123 "));
    }



    @Test
    void testValidateHotel_WithValidData() throws Exception {
        String validHotelJson = 
            "{"
            + "\"Id\": \"hotel123\","
            + "\"destination_id\": \"456\","
            + "\"name\": \"Test Hotel\","
            + "\"location\": {"
            + "\"address\": \"123 Test Street\""
            + "}"
            + "}";
        
        JsonNode hotelNode = objectMapper.readTree(validHotelJson);
        assertTrue(DataHelper.validateHotel(hotelNode));
    }


    @Test
    void testValidateHotel_WithMissingId() throws Exception {
        String invalidHotelJson = 
            "{"
            + "\"destination_id\": \"456\","
            + "\"name\": \"Test Hotel\","
            + "\"location\": {"
            + "\"address\": \"123 Test Street\""
            + "}"
            + "}";
        
        JsonNode hotelNode = objectMapper.readTree(invalidHotelJson);
        // This will throw an exception due to missing Id field
        assertThrows(NullPointerException.class, () -> {
            DataHelper.validateHotel(hotelNode);
        });
    }


    @Test
    void testValidateHotel_WithEmptyName() throws Exception {
        String invalidHotelJson = 
            "{"
            + "\"Id\": \"hotel123\","
            + "\"destination_id\": \"456\","
            + "\"name\": \"\","
            + "\"location\": {"
            + "\"address\": \"123 Test Street\""
            + "}"
            + "}";
        
        JsonNode hotelNode = objectMapper.readTree(invalidHotelJson);
        assertFalse(DataHelper.validateHotel(hotelNode));
    }


    @Test
    void testValidateHotel_WithMissingAddress() throws Exception {
        String invalidHotelJson = 
            "{"
            + "\"Id\": \"hotel123\","
            + "\"destination_id\": \"456\","
            + "\"name\": \"Test Hotel\","
            + "\"location\": {}"
            + "}";
        
        JsonNode hotelNode = objectMapper.readTree(invalidHotelJson);
        // This will throw an exception due to missing address field
        assertThrows(NullPointerException.class, () -> {
            DataHelper.validateHotel(hotelNode);
        });
    }


    @Test
    void testValidateHotel_WithMissingDestinationId() throws Exception {
        String invalidHotelJson = 
            "{"
            + "\"Id\": \"hotel123\","
            + "\"name\": \"Test Hotel\","
            + "\"location\": {"
            + "\"address\": \"123 Test Street\""
            + "}"
            + "}";
        
        JsonNode hotelNode = objectMapper.readTree(invalidHotelJson);
        // This will throw an exception due to missing destination_id field
        assertThrows(NullPointerException.class, () -> {
            DataHelper.validateHotel(hotelNode);
        });
    }


    @Test
    void testIsNullOrEmpty_WithSpecialCharacters() {
        assertFalse(DataHelper.isNullOrEmpty("@#$%^&*()"));
        assertFalse(DataHelper.isNullOrEmpty("\u2603"));  // Unicode snowman
        assertFalse(DataHelper.isNullOrEmpty("Hello\nWorld"));
    }
}