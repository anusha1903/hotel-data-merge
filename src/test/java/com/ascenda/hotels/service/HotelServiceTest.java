package com.ascenda.hotels.service;

import com.ascenda.hotels.integration.DataIntegration;
import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.ascenda.hotels.model.response.Location;
import com.ascenda.hotels.model.response.Amenities;
import com.ascenda.hotels.model.response.Images;
import com.ascenda.hotels.repository.HotelRepository;
import com.ascenda.hotels.repository.entity.Hotel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HotelServiceImpl} class.
 * Tests hotel service operations including data retrieval, storage, and processing.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private DataIntegration dataIntegration;

    @Mock
    private HotelRepository hotelRepository;

    private HotelServiceImpl hotelService;

    private Hotel mockHotel;
    private Response mockResponse;
    private ResponseItem mockResponseItem;

    @BeforeEach
    void setUp() {
        hotelService = new HotelServiceImpl(dataIntegration, hotelRepository);
        
        mockHotel = createMockHotel();
        mockResponseItem = createMockResponseItem();
        mockResponse = createMockResponse();
    }

    /**
     * Should retrieve all hotels from repository
     * Validates successful retrieval of all hotels from the repository.
     */
    @Test
    void testGetAllHotels_Success() {
        List<Hotel> expectedHotels = Arrays.asList(mockHotel, createMockHotel("hotel2"));
        when(hotelRepository.findAll()).thenReturn(expectedHotels);

        List<Hotel> actualHotels = hotelService.getAllHotels();

        assertEquals(2, actualHotels.size());
        assertEquals(expectedHotels, actualHotels);
        verify(hotelRepository, times(1)).findAll();
    }

    /**
     * Should return empty list when no hotels exist
     * Validates behavior when no hotels exist in the repository.
     */
    @Test
    void testGetAllHotels_EmptyRepository() {
        when(hotelRepository.findAll()).thenReturn(new ArrayList<>());

        List<Hotel> actualHotels = hotelService.getAllHotels();

        assertTrue(actualHotels.isEmpty());
        verify(hotelRepository, times(1)).findAll();
    }

    /**
     * Validates successful retrieval of hotel by ID.
     */
    @Test
    void testGetHotelById_ValidId() {
        String hotelId = "hotel123";
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(mockHotel));

        Optional<Hotel> result = hotelService.getHotelById(hotelId);

        assertTrue(result.isPresent());
        assertEquals(mockHotel, result.get());
        verify(hotelRepository, times(1)).findById(hotelId);
    }

    /**
     * return empty Optional for invalid ID
     */
    @Test
    @DisplayName("Should return empty Optional for invalid ID")
    void testGetHotelById_InvalidId() {
        String invalidId = "invalid123";
        when(hotelRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<Hotel> result = hotelService.getHotelById(invalidId);

        assertFalse(result.isPresent());
        verify(hotelRepository, times(1)).findById(invalidId);
    }

    /**
     * Validates behavior when null ID is provided.
     */
    @Test
    void testGetHotelById_NullId() {
        when(hotelRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Hotel> result = hotelService.getHotelById(null);

        assertFalse(result.isPresent());
        verify(hotelRepository, times(1)).findById(null);
    }

    /**
     * Validates successful retrieval of hotels by destination ID.
     */
    @Test
    void testGetHotelsByDestinationId_Success() {
        Long destinationId = 123L;
        List<Hotel> expectedHotels = Arrays.asList(mockHotel);
        when(hotelRepository.findByDestinationId(destinationId)).thenReturn(expectedHotels);

        List<Hotel> actualHotels = hotelService.getHotelsByDestinationId(destinationId);

        assertEquals(1, actualHotels.size());
        assertEquals(expectedHotels, actualHotels);
        verify(hotelRepository, times(1)).findByDestinationId(destinationId);
    }

    /**
     * Validates behavior when no hotels exist for given destination ID.
     */
    @Test
    void testGetHotelsByDestinationId_NoResults() {
        Long destinationId = 999L;
        when(hotelRepository.findByDestinationId(destinationId)).thenReturn(new ArrayList<>());

        List<Hotel> actualHotels = hotelService.getHotelsByDestinationId(destinationId);

        assertTrue(actualHotels.isEmpty());
        verify(hotelRepository, times(1)).findByDestinationId(destinationId);
    }

    /**
     * Validates successful data integration and storage workflow.
     */
    @Test
    void testReadAndStoreDataFromSrc_Success() {
        when(dataIntegration.processUrls()).thenReturn(mockResponse);
        when(hotelRepository.saveAll(anyList())).thenReturn(List.of(mockHotel));

        String result = hotelService.readAndStoreDataFromSrc();

        assertEquals("Data saved successfully. Total hotels: 1", result);
        verify(dataIntegration, times(1)).processUrls();
        verify(hotelRepository, times(1)).saveAll(anyList());
    }

    /**
     * Validates data integration failure
     */
    @Test
    void testReadAndStoreDataFromSrc_IntegrationFailure() {
        when(dataIntegration.processUrls()).thenThrow(new RuntimeException("Data Integration failed"));

        assertThrows(RuntimeException.class, () -> hotelService.readAndStoreDataFromSrc());
    }

    /**
     * Validates successful saving of hotel data from response object.
     */
    @Test
    void testSaveHotelData_ValidResponse() {
        when(hotelRepository.saveAll(anyList())).thenReturn(List.of(mockHotel));
        String result = hotelService.saveHotelData(mockResponse);

        assertEquals("Data saved successfully. Total hotels: 1", result);
    }

    /**
     * Validates behavior when null response is provided.
     */
    @Test
    void testSaveHotelData_NullResponse() {
        String result = hotelService.saveHotelData(null);

        assertEquals("No data to save", result);
    }

    /**
     * Validates behavior when response has no hotel data.
     */
    @Test
    @DisplayName("Should handle empty response gracefully")
    void testSaveHotelData_EmptyResponse() {
        Response emptyResponse = new Response();
        emptyResponse.setResponse(new ArrayList<>());
        when(hotelRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        String result = hotelService.saveHotelData(emptyResponse);

        assertEquals("Data saved successfully. Total hotels: 0", result);
        verify(hotelRepository, times(1)).saveAll(anyList());
    }

    /**
     * Validates behavior when response object has null response items.
     */
    @Test
    void testSaveHotelData_NullResponseItems() {
        Response responseWithNullItems = new Response();
        responseWithNullItems.setResponse(null);


        String result = hotelService.saveHotelData(responseWithNullItems);

        assertEquals("No data to save", result);
        verify(hotelRepository, never()).saveAll(anyList());
    }

    /**
     * Validates error handling when  save operation fails.
     */
    @Test
    @DisplayName("Should handle save failure")
    void testSaveHotelData_SaveFailure() {
        when(hotelRepository.saveAll(anyList())).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> hotelService.saveHotelData(mockResponse));
        verify(hotelRepository, times(1)).saveAll(anyList());
    }


    // Helper methods for creating mock objects

    private Hotel createMockHotel() {
        return createMockHotel("hotel123");
    }

    private Hotel createMockHotel(String id) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setDestinationId(123L);
        hotel.setName("Test Hotel");
        hotel.setDescription("A test hotel");
        hotel.setLat(45.0);
        hotel.setLng(-73.0);
        hotel.setAddress("123 Test St");
        hotel.setCity("Test City");
        hotel.setCountry("Test Country");
        
        Map<String, List<String>> amenities = new HashMap<>();
        amenities.put("general", Arrays.asList("pool", "gym"));
        amenities.put("room", Arrays.asList("tv", "wifi"));
        hotel.setAmenities(amenities);
        
        hotel.setBookingConditions(Arrays.asList("No smoking", "Check-in after 3pm"));
        return hotel;
    }

    private ResponseItem createMockResponseItem() {
        ResponseItem item = new ResponseItem();
        item.setId("hotel123");
        item.setDestinationId(123);
        item.setName("Test Hotel");
        item.setDescription("A test hotel");
        
        Location location = new Location();
        location.setLat(45.0);
        location.setLng(-73.0);
        location.setAddress("123 Test St");
        location.setCity("Test City");
        location.setCountry("Test Country");
        item.setLocation(location);
        
        Amenities amenities = new Amenities();
        amenities.setGeneral(Arrays.asList("pool", "gym"));
        amenities.setRoom(Arrays.asList("tv", "wifi"));
        item.setAmenities(amenities);
        
        item.setImages(new Images());
        item.setBookingConditions(Arrays.asList("No smoking"));
        
        return item;
    }

    private Response createMockResponse() {
        Response response = new Response();
        response.setResponse(Arrays.asList(mockResponseItem));
        return response;
    }

    private Response createComplexMockResponse() {
        Response response = new Response();
        ResponseItem item1 = createMockResponseItem();
        ResponseItem item2 = createMockResponseItem();
        item2.setId("hotel456");
        response.setResponse(Arrays.asList(item1, item2));
        return response;
    }

    private ResponseItem createDetailedResponseItem() {
        ResponseItem item = new ResponseItem();
        item.setId("detailed123");
        item.setDestinationId(456);
        item.setName("Detailed Test Hotel");
        item.setDescription("A luxurious hotel for testing");
        
        Location location = new Location();
        location.setLat(45.123);
        location.setLng(-73.456);
        location.setAddress("123 Test Avenue");
        location.setCity("Test City");
        location.setCountry("Test Country");
        item.setLocation(location);
        
        Amenities amenities = new Amenities();
        amenities.setGeneral(Arrays.asList("spa", "restaurant", "concierge"));
        amenities.setRoom(Arrays.asList("minibar", "safe", "balcony"));
        item.setAmenities(amenities);
        
        item.setImages(new Images());
        item.setBookingConditions(Arrays.asList("No pets", "Non-smoking", "Valid ID required"));
        
        return item;
    }
}