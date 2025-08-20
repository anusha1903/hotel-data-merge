package com.ascenda.hotels.integration;

import com.ascenda.hotels.model.response.Location;
import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests data parsing and merging functionality for hotel data.
 *
 */
@ExtendWith(MockitoExtension.class)
class DataParserTest {

    @Mock
    private DataMerger dataMerger;

    @InjectMocks
    private DataParser dataParser;

    private Map<String, JsonNode> mockDataMap;
    private ObjectMapper mapper;
    private ResponseItem mockResponseItem;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper();
        mockDataMap = new HashMap<>();
        
        // Create mock JSON nodes for different sources
        JsonNode acmeNode = mapper.readTree("[{\"Id\":\"1\",\"DestinationId\":\"5432\",\"Name\":\"Beach Resort\",\"Latitude\":1.264751,\"Longitude\":103.824006}]");
        JsonNode patagoniaNode = mapper.readTree("[{\"id\":\"1\",\"destination\":\"5432\",\"name\":\"Beach Resort\",\"lat\":1.264751,\"lng\":103.824006}]");
        JsonNode paperfliesNode = mapper.readTree("[{\"hotel_id\":\"1\",\"destination_id\":\"5432\",\"hotel_name\":\"Beach Resort\",\"location\":{\"lat\":1.264751,\"lng\":103.824006}}]");
        
        mockDataMap.put("acme", acmeNode);
        mockDataMap.put("patagonia", patagoniaNode);
        mockDataMap.put("paperflies", paperfliesNode);
        
        // Create mock response item
        mockResponseItem = new ResponseItem();
        mockResponseItem.setId("1");
        mockResponseItem.setDestinationId(5432);
        mockResponseItem.setName("Beach Resort");
    }

    /**
     * Validates successful parsing and merging of hotel data from multiple sources.
     */
    @Test
    void testGetMergedHotelData_Success() {
        when(dataMerger.getProcessedHotelData(any())).thenReturn(mockResponseItem);

        Response result = dataParser.getMergedHotelData(mockDataMap);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertFalse(result.getResponse().isEmpty());
    }

    /**
     * Validates behavior when no data is provided for processing.
     */
    @Test
    void testGetMergedHotelData_EmptyDataMap() {
        Map<String, JsonNode> emptyDataMap = new HashMap<>();

        Response result = dataParser.getMergedHotelData(emptyDataMap);

        assertNotNull(result);
        assertTrue(result.getResponse().isEmpty());
    }

    /**
     * Validates error handling when null data is provided.
     */
    @Test
    void testGetMergedHotelData_NullDataMap() {
        assertThrows(NullPointerException.class, () -> {
            dataParser.getMergedHotelData(null);
        });
    }

    /**
     * Validates processing when only one data source is available.
     */
    @Test
    void testGetMergedHotelData_SingleSource() throws Exception {
        Map<String, JsonNode> singleSourceMap = new HashMap<>();
        JsonNode acmeNode = mapper.readTree("[{\"Id\":\"1\",\"DestinationId\":\"5432\",\"Name\":\"Beach Resort\"}]");
        singleSourceMap.put("acme", acmeNode);
        
        when(dataMerger.getProcessedHotelData(any())).thenReturn(mockResponseItem);

        Response result = dataParser.getMergedHotelData(singleSourceMap);

        assertNotNull(result);
        assertNotNull(result.getResponse());
    }


    /**
     * Validates processing of multiple hotel records from a single source.
     */
    @Test
    void testGetMergedHotelData_MultipleHotels() throws Exception {
        Map<String, JsonNode> multiHotelDataMap = new HashMap<>();
        JsonNode multiHotelNode = mapper.readTree("[" +
            "{\"Id\":\"1\",\"DestinationId\":\"5432\",\"Name\":\"Beach Resort\"}," +
            "{\"Id\":\"2\",\"DestinationId\":\"5432\",\"Name\":\"City Hotel\"}" +
            "]");
        multiHotelDataMap.put("acme", multiHotelNode);
        
        when(dataMerger.getProcessedHotelData(any())).thenReturn(mockResponseItem);

        Response result = dataParser.getMergedHotelData(multiHotelDataMap);

        assertNotNull(result);
        assertNotNull(result.getResponse());
        verify(dataMerger, atLeast(2)).getProcessedHotelData(any());
    }


}