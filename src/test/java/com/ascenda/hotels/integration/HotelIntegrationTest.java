package com.ascenda.hotels.integration;

import com.ascenda.hotels.api.HotelController;
import com.ascenda.hotels.model.request.HotelRequest;
import com.ascenda.hotels.repository.entity.Hotel;
import com.ascenda.hotels.service.HotelServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests complete workflows from HTTP requests through service layer operations.
 *
 */
@ExtendWith(MockitoExtension.class)
class HotelIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private HotelServiceImpl hotelService;

    private HotelController hotelController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        hotelController = new HotelController(hotelService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(hotelController)
                .build();
        objectMapper = new ObjectMapper();
    }

    /**
     * Validates end-to-end data processing integration.
     */
    @Test
    void testCompleteDataIngestionWorkflow() throws Exception {
        when(hotelService.readAndStoreDataFromSrc()).thenReturn("Data ingestion completed successfully");

        mockMvc.perform(get("/api/hotels/ingestHotelData"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data ingestion completed successfully"));

        verify(hotelService, times(1)).readAndStoreDataFromSrc();
    }

    /**
     * Validates end-to-end hotel search and retrieval functionality.
     */
    @Test
    @DisplayName("Should successfully retrieve all hotels through REST API")
    void testCompleteHotelRetrievalWorkflow() throws Exception {
        List<Hotel> testHotels = Arrays.asList(
            createIntegrationTestHotel("hotel-1", "Grand Hotel"),
            createIntegrationTestHotel("hotel-2", "Boutique Inn")
        );
        when(hotelService.getAllHotels()).thenReturn(testHotels);

        mockMvc.perform(get("/api/hotels/getAllHotels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("hotel-1"))
                .andExpect(jsonPath("$[0].name").value("Grand Hotel"))
                .andExpect(jsonPath("$[1].id").value("hotel-2"))
                .andExpect(jsonPath("$[1].name").value("Boutique Inn"));

        verify(hotelService, times(1)).getAllHotels();
    }

    /**
     * Validates end-to-end hotel search functionality using hotel ID.
     */
    @Test
    @DisplayName("Should successfully search hotels by ID through REST API")
    void testHotelSearchByIdWorkflow() throws Exception {
        Hotel testHotel = createIntegrationTestHotel("search-hotel-123", "Search Test Hotel");
        when(hotelService.getHotelById("search-hotel-123")).thenReturn(Optional.of(testHotel));

        HotelRequest searchRequest = new HotelRequest();
        searchRequest.setId("search-hotel-123");

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("search-hotel-123"));

        verify(hotelService, times(1)).getHotelById("search-hotel-123");
    }

    /**
     * Validates end-to-end hotel search functionality using destination ID.
     */
    @Test
    void testHotelSearchByDestinationIdWorkflow() throws Exception {
        List<Hotel> destinationHotels = Arrays.asList(
            createIntegrationTestHotel("dest-hotel-1", "Destination Hotel 1"),
            createIntegrationTestHotel("dest-hotel-2", "Destination Hotel 2")
        );
        when(hotelService.getHotelsByDestinationId(98765L)).thenReturn(destinationHotels);

        HotelRequest searchRequest = new HotelRequest();
        searchRequest.setDestinationId(98765L);

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("dest-hotel-1"))
                .andExpect(jsonPath("$[1].id").value("dest-hotel-2"));

        verify(hotelService, times(1)).getHotelsByDestinationId(98765L);
    }

    // Helper methods for creating test data

    private Hotel createIntegrationTestHotel(String id, String name) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setDestinationId(12345L);
        hotel.setName(name);
        hotel.setDescription("Integration test hotel for end-to-end testing");
        hotel.setLat(45.5017);
        hotel.setLng(-73.5673);
        hotel.setAddress("123 Integration Test Avenue");
        hotel.setCity("Test Montreal");
        hotel.setCountry("Test Canada");

        Map<String, List<String>> amenities = new HashMap<>();
        amenities.put("general", Arrays.asList("wifi", "pool", "fitness center", "restaurant"));
        amenities.put("room", Arrays.asList("tv", "air conditioning", "minibar", "safe"));
        hotel.setAmenities(amenities);

        hotel.setBookingConditions(Arrays.asList(
            "Non-smoking property",
            "Check-in: 3:00 PM",
            "Check-out: 11:00 AM",
            "Valid ID required"
        ));

        return hotel;
    }

    private Hotel createDetailedIntegrationTestHotel() {
        Hotel hotel = createIntegrationTestHotel("detailed-integration-hotel", "Detailed Integration Test Hotel");

        hotel.setDescription("A comprehensive luxury hotel with extensive amenities and services, " +
                           "perfect for integration testing with all possible data fields populated");

        Map<String, List<String>> extendedAmenities = new HashMap<>();
        extendedAmenities.put("general", Arrays.asList(
            "wifi", "pool", "fitness center", "restaurant", "spa", "concierge",
            "valet parking", "business center", "conference rooms", "rooftop bar"
        ));
        extendedAmenities.put("room", Arrays.asList(
            "tv", "air conditioning", "minibar", "safe", "room service",
            "balcony", "kitchenette", "jacuzzi", "work desk", "premium bedding"
        ));
        hotel.setAmenities(extendedAmenities);

        hotel.setBookingConditions(Arrays.asList(
            "Non-smoking property throughout",
            "Check-in: 3:00 PM - 11:00 PM",
            "Check-out: 11:00 AM",
            "Valid government-issued photo ID and credit card required",
            "Pet-friendly with additional fees and restrictions",
            "Cancellation policy: Free cancellation up to 24 hours before arrival",
            "Late check-out available upon request and subject to availability",
            "Minimum age for check-in: 21 years"
        ));

        return hotel;
    }
}