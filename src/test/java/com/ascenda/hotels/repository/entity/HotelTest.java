package com.ascenda.hotels.repository.entity;

import com.ascenda.hotels.model.response.Images;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests entity getters, setters, and data integrity.
 */
class HotelTest {

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
    }

    /**
     * Validates that all basic properties can be set and retrieved correctly.
     */
    @Test
    void testBasicProperties() {
        String id = "hotel123";
        Long destinationId = 456L;
        String name = "Test Hotel";
        String description = "A beautiful test hotel";
        Double lat = 45.123;
        Double lng = -73.456;
        String address = "123 Test Street";
        String city = "Test City";
        String country = "Test Country";

        hotel.setId(id);
        hotel.setDestinationId(destinationId);
        hotel.setName(name);
        hotel.setDescription(description);
        hotel.setLat(lat);
        hotel.setLng(lng);
        hotel.setAddress(address);
        hotel.setCity(city);
        hotel.setCountry(country);

        assertEquals(id, hotel.getId());
        assertEquals(destinationId, hotel.getDestinationId());
        assertEquals(name, hotel.getName());
        assertEquals(description, hotel.getDescription());
        assertEquals(lat, hotel.getLat());
        assertEquals(lng, hotel.getLng());
        assertEquals(address, hotel.getAddress());
        assertEquals(city, hotel.getCity());
        assertEquals(country, hotel.getCountry());
    }

}