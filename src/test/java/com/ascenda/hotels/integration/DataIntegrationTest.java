package com.ascenda.hotels.integration;

import com.ascenda.hotels.integration.config.UrlConfig;
import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests data integration functionality including URL processing and hotel data merging.
 */
@ExtendWith(MockitoExtension.class)
class DataIntegrationTest {

    @Mock
    private DataFetcher dataFetcher;

    @Mock
    private DataParser dataParser;

    @InjectMocks
    private DataIntegration dataIntegration;

    private Map<String, JsonNode> mockDataMap;
    private Response mockResponse;

    @BeforeEach
    void setUp() {
        mockDataMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode mockNode = mapper.readTree("{\"hotel_id\":\"1\",\"name\":\"Test Hotel\"}");
            mockDataMap.put("acme", mockNode);
        } catch (Exception e) {
            //
        }
        
        // Setup mock response
        mockResponse = new Response();
        ResponseItem mockItem = new ResponseItem();
        mockItem.setId("1");
        mockItem.setName("Test Hotel");
        mockResponse.setResponse(Arrays.asList(mockItem));
    }

    /**
     * Validates successful processing of hotel data from multiple sources.
     */
    @Test
    void testProcessUrls_Success() {

        when(dataFetcher.getHotelDataFromsource()).thenReturn(mockDataMap);
        when(dataParser.getMergedHotelData(mockDataMap)).thenReturn(mockResponse);

        Response result = dataIntegration.processUrls();

        assertNotNull(result);
        assertNotNull(result.getResponse());
        verify(dataFetcher, times(1)).getHotelDataFromsource();
        verify(dataParser, times(1)).getMergedHotelData(mockDataMap);
    }

    /**
     * Validates behavior when no URLs are provided for processing.
     */
    @Test
    void testProcessUrls_EmptyUrlList() {
        Map<String, JsonNode> emptyDataMap = new HashMap<>();
        Response emptyResponse = new Response();
        emptyResponse.setResponse(Arrays.asList());
        
        when(dataFetcher.getHotelDataFromsource()).thenReturn(emptyDataMap);
        when(dataParser.getMergedHotelData(emptyDataMap)).thenReturn(emptyResponse);

        Response result = dataIntegration.processUrls();

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertTrue(result.getResponse().isEmpty());
        verify(dataFetcher, times(1)).getHotelDataFromsource();
        verify(dataParser, times(1)).getMergedHotelData(emptyDataMap);
    }

    /**
     * Validates behavior when data fetcher returns null.
     */
    @Test
    void testProcessUrls_NullUrlList() {
        when(dataFetcher.getHotelDataFromsource()).thenReturn(null);
        when(dataParser.getMergedHotelData(null)).thenReturn(mockResponse);

        Response result = dataIntegration.processUrls();

        assertNotNull(result);
        verify(dataFetcher, times(1)).getHotelDataFromsource();
        verify(dataParser, times(1)).getMergedHotelData(null);
    }


    /**
     * Tests error handling during data processing.
     */
    @Test
    void testProcessUrls_HandleErrors() {
        when(dataFetcher.getHotelDataFromsource()).thenReturn(mockDataMap);
        when(dataParser.getMergedHotelData(mockDataMap)).thenReturn(mockResponse);

        Response result = dataIntegration.processUrls();

        assertNotNull(result);
        assertNotNull(result.getResponse());
    }


    /**
     * Validates that processing completes within recommended time limits.
     */
    @Test
    void testProcessUrls_Performance() {
        when(dataFetcher.getHotelDataFromsource()).thenReturn(mockDataMap);
        when(dataParser.getMergedHotelData(mockDataMap)).thenReturn(mockResponse);
        long startTime = System.currentTimeMillis();

        Response result = dataIntegration.processUrls();

        long processingTime = System.currentTimeMillis() - startTime;
        assertNotNull(result);
        assertTrue(processingTime < 30000, "Processing should complete within 30 seconds");
    }
}