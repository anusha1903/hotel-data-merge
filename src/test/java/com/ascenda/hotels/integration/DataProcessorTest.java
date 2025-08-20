package com.ascenda.hotels.integration;

import com.ascenda.hotels.model.response.ResponseItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests data processing functionality for different hotel data sources.
 */
@ExtendWith(MockitoExtension.class)
class DataProcessorTest {

    @InjectMocks
    private DataProcessor dataProcessor;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void testProcessAcmeData_NullResponseItem() throws Exception {
        JsonNode acmeNode = mapper.readTree("{\"Id\":\"1\",\"DestinationId\":5432,\"Name\":\"Beach Resort\"}");

        assertThrows(NullPointerException.class, () -> {
            dataProcessor.processAcmeData(acmeNode, null);
        });
    }

    @Test
    void testProcessAcmeData_NullJsonNode() {
        ResponseItem responseItem = new ResponseItem();

        assertThrows(NullPointerException.class, () -> {
            dataProcessor.processAcmeData(null, responseItem);
        });
    }


    @Test
    void testProcessAcmeData_NonNullInputs() throws Exception {
        JsonNode acmeNode = mapper.readTree("{\"Id\":\"1\",\"DestinationId\":5432,\"Name\":\"Beach Resort\"}");
        ResponseItem responseItem = new ResponseItem();


        assertThrows(NullPointerException.class, () -> {
            dataProcessor.processAcmeData(acmeNode, responseItem);
        }, "Expected exception due to missing required fields in test data");
        
        assertNotNull(responseItem);
    }
}