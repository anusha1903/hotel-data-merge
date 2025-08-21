package com.ascenda.hotels.integration;

import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.ascenda.hotels.util.DataHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataParser {

    private static final Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    private final DataMerger dataMerger;

    public DataParser(DataMerger dataMerger) {
        this.dataMerger = dataMerger;
    }

    /**
     * As we have to merge the hotel data based on the hotel_id, rebuild the map so that key - hotel-id, value - hotel object from each source
     * This enables us to read, clean, parse, and merge data for each hotel_id
     *
     * @param dataMap
     * @return
     */
    public Response getMergedHotelData(Map<String, JsonNode> dataMap){
        logger.info("Rebuilding hotel data structure");
        Map<String,Map<String,JsonNode>> hotelData =  buildHotelData(dataMap);
        logger.info("Processing consolidated hotel data");
        // responseitem -> holds merged of one hotel data from all the upstreams
        // list<responseitem> -> response -> holds all the merged hotels data
        Response response = new Response();
        List<ResponseItem> responseItems = new ArrayList<>();
        hotelData.forEach((key, eachHotelData) -> {
            logger.debug("Processing hotel with key: {}", key);
            Map<String,JsonNode> value = hotelData.get(key);
            ResponseItem responseItem = dataMerger.getProcessedHotelData(value);
            ObjectMapper mapper = new ObjectMapper();

//            // Convert the POJO to a JsonNode
//            JsonNode jsonNode = mapper.valueToTree(responseItem);
//
//            // Print the JsonNode (which will implicitly call its toString() method)
//            System.out.println(jsonNode);
            responseItems.add(responseItem);
        });
        response.setResponse(responseItems);
        return response;
    }

    private Map<String,Map<String,JsonNode>> buildHotelData(Map<String, JsonNode> dataMap){
        Map<String,Map<String,JsonNode>> hotelDataMap = buildHotelDataMap(dataMap);

        hotelDataMap.forEach((key, hotelData) -> {
            logger.debug("Processing hotel with key: {}", key);
            Map<String,JsonNode> value = hotelDataMap.get(key);
            value.entrySet().stream().forEach(entry -> {
                logger.debug("Data source {}: {}", entry.getKey(), entry.getValue().toPrettyString());
            });
        });

        return hotelDataMap;
    }

    /**
     * structure:
     * say for "acme" hotel data: hotelId - abc, destinatonId - 123
     * hotelKey : abc-123
     * hoteldqtaMap -> <hotelkey,<key,hotelobject>>
     *     i.e., <"abc-123",<"acme", {acme_hotel_object_jsonNode}>>
     * Each upstream like acme has different json structure. In this case, for each hotel_id of a specific destination_id,
     * as we have to traverse through all upstreams and merge the data, we are storing with upstream name as key against the
     * hotel_object for every hotel_key
     * @param dataMap
     * @return
     */
    private Map<String,Map<String,JsonNode>> buildHotelDataMap(Map<String, JsonNode> dataMap){

        Map<String,Map<String,JsonNode>> hotelDataMap = new HashMap<>();
        dataMap.forEach((key, hotelData) -> {

            if(hotelData.isArray()){
                for (JsonNode eachHotelObject : hotelData) {
                    String hotelId = null;
                    if(key.equalsIgnoreCase("acme")){
                        hotelId = eachHotelObject.get("Id").asText();
                    } else if(key.equalsIgnoreCase("patagonia")){
                        hotelId = eachHotelObject.get("id").asText();
                    } else if(key.equalsIgnoreCase("paperflies")){
                        hotelId = eachHotelObject.get("hotel_id").asText();
                    } else {
                        logger.warn("Unknown data source key: {}", key);
                    }

                    if(!DataHelper.isNullOrEmpty(hotelId)){
                        if(hotelDataMap.containsKey(hotelId)){
                            Map<String, JsonNode> hotelObjects = hotelDataMap.get(hotelId);
                            hotelObjects.put(key, eachHotelObject);
                            hotelDataMap.put(hotelId, hotelObjects);
                        } else {
                            hotelDataMap.put(hotelId, new HashMap<>(Map.of(key,eachHotelObject)));
                        }
                    } else {
                        logger.warn("Invalid hotel data - null or empty hotel ID for source: {}", key);
                    }

                }
            }
        });
        return hotelDataMap;
    }


}
