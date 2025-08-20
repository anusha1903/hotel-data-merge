package com.ascenda.hotels.integration;

import com.ascenda.hotels.integration.config.UrlConfig;
import com.ascenda.hotels.model.response.Location;
import com.ascenda.hotels.model.response.ResponseItem;
import com.ascenda.hotels.repository.entity.Hotel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DataFetcher} service class.
 * Tests data fetching functionality from various URL sources.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class DataFetcherTest {

    @Mock
    private UrlConfig urlConfig;

    @InjectMocks
    private DataFetcher dataFetcher;

    private List<String> mockUrls;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockUrls = Arrays.asList(
                "https://mockUrl/suppliers/acme",
                "https://mockUrl/suppliers/patagonia",
                "https://mockUrl/suppliers/paperflies"
        );
        mapper = new ObjectMapper();
    }

    /**
     * Validates successful retrieval of hotel data from configured sources.
     */
    @Test
    void testGetHotelDataFromsource_Success() {
        when(urlConfig.getUrlList()).thenReturn(mockUrls);

        Map<String, JsonNode> result = dataFetcher.getHotelDataFromsource();

        assertNotNull(result);
        verify(urlConfig, times(1)).getUrlList();
    }

    /**
     * Validates behavior when no URLs are configured.
     */
    @Test
    void testGetHotelDataFromsource_EmptyUrls() {
        when(urlConfig.getUrlList()).thenReturn(Arrays.asList());
        Map<String, JsonNode> result = dataFetcher.getHotelDataFromsource();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(urlConfig, times(1)).getUrlList();
    }


    /**
     * Validates handling of empty input.
     */
    @Test
    void testGetHotelData_EmptyList() {
        Map<String, JsonNode> result = dataFetcher.getHotelData(Arrays.asList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    /**
     * Validates that invalid URLs don't break the processing flow.
     */
    @Test
    void testGetHotelData_InvalidUrls() {
        List<String> invalidUrls = Arrays.asList(
            "invalid-url",
            "https://nonexistent-domain-12345.com/data"
        );

        assertDoesNotThrow(() -> {
            Map<String, JsonNode> result = dataFetcher.getHotelData(invalidUrls);
            assertNotNull(result);
        });
    }

    /**
     * Validates behavior when URL configuration returns null.
     */
    @Test
    void testGetHotelDataFromsource_NullUrls() {
        when(urlConfig.getUrlList()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            dataFetcher.getHotelDataFromsource();
        });
    }

}