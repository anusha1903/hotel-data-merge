package com.ascenda.hotels.model.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HotelRequest} model class.
 * Tests request object getters, setters, and validation logic.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
class HotelRequestTest {

    private HotelRequest hotelRequest;

    @BeforeEach
    void setUp() {
        hotelRequest = new HotelRequest();
    }

    /**
     * Validates that ID can be set and retrieved correctly.
     */
    @Test
    void testIdProperty() {
        String hotelId = "hotel123";

        hotelRequest.setId(hotelId);
        assertEquals(hotelId, hotelRequest.getId());
    }

    /**
     * Validates that destination ID can be set and retrieved correctly.
     */
    @Test
    void testDestinationIdProperty() {
        Long destinationId = 456L;

        hotelRequest.setDestinationId(destinationId);

        assertEquals(destinationId, hotelRequest.getDestinationId());
    }

    /**
     * Validates that the request object can handle null values.
     */
    @Test
    void testNullValues() {
        hotelRequest.setId(null);
        hotelRequest.setDestinationId(null);

        assertNull(hotelRequest.getId());
        assertNull(hotelRequest.getDestinationId());
    }

    /**
     * Validates handling of empty string values.
     */
    @Test
    void testEmptyStringId() {
        String emptyId = "";

        hotelRequest.setId(emptyId);

        assertEquals(emptyId, hotelRequest.getId());
        assertTrue(hotelRequest.getId().isEmpty());
    }
}