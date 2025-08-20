package com.ascenda.hotels.integration;

import com.ascenda.hotels.model.response.ResponseItem;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataMerger {

    private static final Logger logger = LoggerFactory.getLogger(DataMerger.class);

    private final DataProcessor dataProcessor;
    public DataMerger(DataProcessor dataProcessor, DataProcessor dataProcessor1) {
        this.dataProcessor = dataProcessor1;
    }

    /**
     * For each hotel data from all the sources, create responseItem
     * before assigning to responseItem attribute, clean the data. So that while merging, only cleaned data is compared
     * cleaning data :
     * @param eachHotelDataFromAllSources
     * @return
     */
    public ResponseItem getProcessedHotelData(Map<String, JsonNode> eachHotelDataFromAllSources) {
        ResponseItem responseItem = new ResponseItem();

        if (eachHotelDataFromAllSources.containsKey("acme")) {
            logger.debug("Processing acme data source");
            try {
                JsonNode acmeNode = eachHotelDataFromAllSources.get("acme");
                if(acmeNode != null){
                    dataProcessor.processAcmeData(acmeNode, responseItem);
                }
            } catch (Exception e) {
                logger.error("Exception processing acme data: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        if (eachHotelDataFromAllSources.containsKey("patagonia")) {
            try {
                logger.debug("Processing patagonia data source");
                JsonNode patagoniaNode = eachHotelDataFromAllSources.get("patagonia");
                if(patagoniaNode != null) {
                    dataProcessor.processPatagoniaData(patagoniaNode, responseItem);
                }

            } catch (Exception e) {
                logger.error("Exception processing patagonia data: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        if (eachHotelDataFromAllSources.containsKey("paperflies")) {
            try {
                logger.debug("Processing paperflies data source");
                JsonNode paperfliesNode = eachHotelDataFromAllSources.get("paperflies");
                if(paperfliesNode != null) {
                    dataProcessor.processPaperfliesData(paperfliesNode, responseItem);
                }

            } catch (Exception e) {
                logger.error("Exception processing paperflies data: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        //System.out.println("responseItem for hotelId : " + responseItem);

        return  responseItem;
    }

}
