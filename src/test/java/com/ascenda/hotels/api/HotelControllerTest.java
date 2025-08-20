package com.ascenda.hotels.api;

import com.ascenda.hotels.exceptionHandler.DataIngestionException;
import com.ascenda.hotels.model.request.HotelRequest;
import com.ascenda.hotels.repository.entity.Hotel;
import com.ascenda.hotels.service.HotelServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ascenda.hotels.exceptionHandler.GlobalExceptionHandler;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link HotelController} REST controller.
 * Tests all HTTP endpoints including GET and POST operations for hotel management.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HotelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HotelServiceImpl hotelService;

    private HotelController hotelController;
    private ObjectMapper objectMapper;

    private Hotel mockHotel;
    private HotelRequest validRequest;

    @BeforeEach
    void setUp() {
        hotelController = new HotelController(hotelService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(hotelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        mockHotel = createMockHotel();
        validRequest = createValidHotelRequest();
    }

    /**
     * /api/hotels/getAllHotels should return all hotels
     * Validates that all hotels are returned with HTTP 200 status.
     */
    @Test
    void testGetAllHotels_Success() throws Exception {

        List<Hotel> hotels = Arrays.asList(mockHotel, createMockHotel("hotel2"));
        when(hotelService.getAllHotels()).thenReturn(hotels);

        mockMvc.perform(get("/api/hotels/getAllHotels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("hotel123"))
                .andExpect(jsonPath("$[0].name").value("Test Hotel"))
                .andExpect(jsonPath("$[1].id").value("hotel2"));

        verify(hotelService, times(1)).getAllHotels();
    }

    /**
     * /api/hotels/getAllHotels should throw exception when no hotels found
     * Validates that HotelNotFoundException is thrown with appropriate message.
     */
    @Test
    void testGetAllHotels_NoHotelsFound() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/hotels/getAllHotels"))
                .andExpect(status().isNotFound());

        verify(hotelService, times(1)).getAllHotels();
    }

    /**
     * /api/hotels/getHotelDetails should return hotel by ID
     * Validates successful retrieval of hotel by ID.
     */
    @Test
    void testGetHotelsForReq_ValidId() throws Exception {
        when(hotelService.getHotelById("hotel123")).thenReturn(Optional.of(mockHotel));

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("hotel123"))
                .andExpect(jsonPath("$[0].name").value("Test Hotel"));

        verify(hotelService, times(1)).getHotelById("hotel123");
    }

    /**
     * /api/hotels/getHotelDetails should return hotels by destination ID
     * Validates successful retrieval of hotels by destination ID.
     */
    @Test
    void testGetHotelsForReq_ValidDestinationId() throws Exception {
        HotelRequest destRequest = new HotelRequest();
        destRequest.setDestinationId(123L);
        
        List<Hotel> hotels = Arrays.asList(mockHotel);
        when(hotelService.getHotelsByDestinationId(123L)).thenReturn(hotels);

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(destRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destinationId").value(123));

        verify(hotelService, times(1)).getHotelsByDestinationId(123L);
    }

    /**
     * /api/hotels/getHotelDetails should throw exception for missing parameters
     * Validates that HotelNotFoundException is thrown when both ID and destination ID are missing.
     */
    @Test
    void testGetHotelsForReq_MissingParameters() throws Exception {
        HotelRequest emptyRequest = new HotelRequest();

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        verify(hotelService, never()).getHotelById(anyString());
        verify(hotelService, never()).getHotelsByDestinationId(anyLong());
    }

    /**
     * /api/hotels/getHotelDetails should throw exception for empty ID
     * Validates that empty ID is treated as missing parameter.
     */
    @Test
    void testGetHotelsForReq_EmptyId() throws Exception {
        HotelRequest emptyIdRequest = new HotelRequest();
        emptyIdRequest.setId("");

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyIdRequest)))
                .andExpect(status().isBadRequest());

        verify(hotelService, never()).getHotelById(anyString());
    }

    /**
     * /api/hotels/getHotelDetails should throw exception for non-existent hotel
     * Validates that HotelNotFoundException is thrown when hotel is not found.
     */
    @Test
    void testGetHotelsForReq_NonExistentHotel() throws Exception {
        when(hotelService.getHotelById("nonexistent")).thenReturn(Optional.empty());
        
        HotelRequest nonExistentRequest = new HotelRequest();
        nonExistentRequest.setId("nonexistent");

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentRequest)))
                .andExpect(status().isNotFound());

        verify(hotelService, times(1)).getHotelById("nonexistent");
    }

    /**
     * /api/hotels/getHotelDetails should throw exception for non-existent destination
     * Validates that HotelNotFoundException is thrown when no hotels found for destination.
     */
    @Test
    void testGetHotelsForReq_NonExistentDestination() throws Exception {
        when(hotelService.getHotelsByDestinationId(999L)).thenReturn(new ArrayList<>());
        
        HotelRequest nonExistentDestRequest = new HotelRequest();
        nonExistentDestRequest.setDestinationId(999L);

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentDestRequest)))
                .andExpect(status().isNotFound());

        verify(hotelService, times(1)).getHotelsByDestinationId(999L);
    }

    /**
     * /api/hotels/ingestHotelData should successfully ingest data
     * Validates that data ingestion endpoint works correctly.
     */
    @Test
    void testSaveAllHotels_Success() throws Exception {
        when(hotelService.readAndStoreDataFromSrc()).thenReturn("Data ingested successfully");

        mockMvc.perform(get("/api/hotels/ingestHotelData"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data ingested successfully"));

        verify(hotelService, times(1)).readAndStoreDataFromSrc();
    }

    /**
     * /api/hotels/ingestHotelData should handle DataIngestionException
     * Validates error handling during data ingestion failure.
     */
    @Test
    @DisplayName("GET /api/hotels/ingestHotelData should handle DataIngestionException")
    void testSaveAllHotels_DataIngestionException() throws Exception {
        when(hotelService.readAndStoreDataFromSrc())
                .thenThrow(new DataIngestionException("Integration failed"));

        mockMvc.perform(get("/api/hotels/ingestHotelData"))
                .andExpect(status().isExpectationFailed());

        verify(hotelService, times(1)).readAndStoreDataFromSrc();
    }


    @Test
    void testSaveAllHotels_GenericException() throws Exception {
        when(hotelService.readAndStoreDataFromSrc())
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/hotels/ingestHotelData"))
                .andExpect(status().isInternalServerError());

        verify(hotelService, times(1)).readAndStoreDataFromSrc();
    }

    /**
     * Tests request validation with malformed JSON.
     * Validates that malformed JSON requests are properly handled.
     */
    @Test
    void testGetHotelsForReq_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"destinationid\":}"))
                .andExpect(status().isBadRequest());

        verify(hotelService, never()).getHotelById(anyString());
        verify(hotelService, never()).getHotelsByDestinationId(anyLong());
    }

    /**
     * Tests request with both ID and destination ID provided.
     * Validates that ID takes precedence when both parameters are provided.
     */
    @Test
    @DisplayName("POST /api/hotels/getHotelDetails should prioritize ID over destination ID")
    void testGetHotelsForReq_BothParameters() throws Exception {
        HotelRequest bothParamsRequest = new HotelRequest();
        bothParamsRequest.setId("hotel123");
        bothParamsRequest.setDestinationId(456L);
        
        when(hotelService.getHotelById("hotel123")).thenReturn(Optional.of(mockHotel));

        mockMvc.perform(post("/api/hotels/getHotelDetails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bothParamsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("hotel123"));

        verify(hotelService, times(1)).getHotelById("hotel123");
        verify(hotelService, never()).getHotelsByDestinationId(anyLong());
    }

    // Helper methods for creating test data

    private Hotel createMockHotel() {
        return createMockHotel("hotel123");
    }

    private Hotel createMockHotel(String id) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setDestinationId(123L);
        hotel.setName("Test Hotel");
        hotel.setDescription("A test hotel for validation");
        hotel.setLat(45.0);
        hotel.setLng(-73.0);
        hotel.setAddress("123 Test Street");
        hotel.setCity("Test City");
        hotel.setCountry("Test Country");
        
        Map<String, List<String>> amenities = new HashMap<>();
        amenities.put("general", Arrays.asList("pool", "gym", "spa"));
        amenities.put("room", Arrays.asList("tv", "wifi", "minibar"));
        hotel.setAmenities(amenities);
        
        hotel.setBookingConditions(Arrays.asList("No smoking", "Check-in after 3pm", "Valid ID required"));
        
        return hotel;
    }

    private HotelRequest createValidHotelRequest() {
        HotelRequest request = new HotelRequest();
        request.setId("hotel123");
        return request;
    }
}