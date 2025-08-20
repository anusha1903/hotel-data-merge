package com.ascenda.hotels.integration;

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
 * Tests data merging functionality for hotel data from different sources.
 */
@ExtendWith(MockitoExtension.class)
class DataMergerTest {

    @Mock
    private DataProcessor dataProcessor;

    @InjectMocks
    private DataMerger dataMerger;

    private Map<String, JsonNode> mockHotelDataMap;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new ObjectMapper();
        mockHotelDataMap = new HashMap<>();
        
        JsonNode acmeNode = mapper.readTree("{\"Id\":\"1\",\"DestinationId\":\"5432\",\"Name\":\"Beach Resort\",\"Latitude\":1.264751,\"Longitude\":103.824006}");
        JsonNode patagoniaNode = mapper.readTree("{\"id\":\"1\",\"destination\":\"5432\",\"name\":\"Beach Resort\",\"lat\":1.264751,\"lng\":103.824006}");
        JsonNode paperfliesNode = mapper.readTree("{\"hotel_id\":\"1\",\"destination_id\":\"5432\",\"hotel_name\":\"Beach Resort\",\"location\":{\"lat\":1.264751,\"lng\":103.824006}}");
        
        mockHotelDataMap.put("acme", acmeNode);
        mockHotelDataMap.put("patagonia", patagoniaNode);
        mockHotelDataMap.put("paperflies", paperfliesNode);
    }

    /**
     * Validate successfully process hotel data from all sources
     * @throws Exception
     */
    @Test
    void testGetProcessedHotelData_AllSources() throws Exception {
        when(dataProcessor.processAcmeData(any(), any())).thenReturn(new ResponseItem());
        when(dataProcessor.processPatagoniaData(any(), any())).thenReturn(new ResponseItem());
        when(dataProcessor.processPaperfliesData(any(), any())).thenReturn(new ResponseItem());

        ResponseItem result = dataMerger.getProcessedHotelData(mockHotelDataMap);

        assertNotNull(result);
        verify(dataProcessor, times(1)).processAcmeData(any(), any());
    }

    /**
     * Validating empty data map
     * @throws Exception
     */
    @Test
    void testGetProcessedHotelData_EmptyMap() throws Exception {
        Map<String, JsonNode> emptyMap = new HashMap<>();

        ResponseItem result = dataMerger.getProcessedHotelData(emptyMap);

        assertNotNull(result);
        verify(dataProcessor, never()).processAcmeData(any(), any());
    }

    /**
     * Validation of exception handling during acme data processing
     * @throws Exception
     */
    @Test
    void testGetProcessedHotelData_AcmeException() throws Exception {
        Map<String, JsonNode> acmeOnlyMap = new HashMap<>();
        acmeOnlyMap.put("acme", mockHotelDataMap.get("acme"));
        when(dataProcessor.processAcmeData(any(), any())).thenThrow(new RuntimeException("Processing error"));

        assertThrows(RuntimeException.class, () -> {
            dataMerger.getProcessedHotelData(acmeOnlyMap);
        });

    }

}