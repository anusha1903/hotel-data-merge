package com.ascenda.hotels.integration;

import com.ascenda.hotels.Util.HotelAmenities;
import com.ascenda.hotels.Util.DataHelper;
import com.ascenda.hotels.Util.MergingStrategies;
import com.ascenda.hotels.integration.config.UrlConfig;
import com.ascenda.hotels.model.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class DataIntegration {

    private final UrlConfig urlConfig;

    public DataIntegration(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }

    //Reading data from upstreams/sources
    // Here the aim is not to create the pojo from json. So jsonnode is used instead of jsonobject

    public void processUrls(){

        List<String> urls = urlConfig.getUrlList();

        Map<String, JsonNode> dataMap = getHotelData(urls);

        // take each upstream data and create a map with hotelId as the key
        //assuminh hotelId would be unique

        Map<String,Map<String,JsonNode>>  hotelDataMap = getMergedHotelData(dataMap);



    }

    private Map<String,Map<String,JsonNode>> getMergedHotelData(Map<String, JsonNode> dataMap){
        Map<String,Map<String,JsonNode>> hotelData =  buildHotelData(dataMap);
        // responseitem -> holds merged of one hotel data from all the upstreams
        // list<responseitem> -> response -> holds all the merged hotels data
        ResponseItem responseItem = new ResponseItem();
        hotelData.forEach((key, eachHotelData) -> {
            System.out.println("hotel key: " + key);
            Map<String,JsonNode> value = hotelData.get(key);
            ResponseItem responseItem1 = getProcessedHotelData(value);
            value.forEach((key1, value1) -> System.out.println(key1 + ": " + value1.toPrettyString()));
        });
        return hotelData;
    }

    /**
     * For each hotel data from all the sources, create responseItem
     * before assigning to responseItem attribute, clean the data. So that while merging, only cleaned data is compared
     * cleaning data :
     * @param eachHotelDataFromAllSources
     * @return
     */
    private ResponseItem getProcessedHotelData(Map<String,JsonNode> eachHotelDataFromAllSources) {
        ResponseItem responseItem = new ResponseItem();

        eachHotelDataFromAllSources.forEach((key, value) -> {
            //process for acme
            JsonNode acmeNode = value.get("acme");
            ResponseItem acmeResponseItem = processAcmeData(acmeNode, responseItem);

            //process for patagonia

            //process for paperflies
        });

        return  responseItem;
    }

    private ResponseItem processAcmeData(JsonNode acmeNode, ResponseItem responseItem){
        responseItem.setId(acmeNode.get("id").asText().trim());
        responseItem.setDestinationId(acmeNode.get("DestinationId").asInt());
        String name = acmeNode.get("Name").asText();
        if(!DataHelper.isNullOrEmpty(name)) {
            if(responseItem.getName() == null) {
                responseItem.setName(name.trim());
            } else {
                String existingName = responseItem.getName();
                responseItem.setName(MergingStrategies.getStringWithMaxLength(existingName, name));
            }
        }
        Location location =  new Location();
        location.setLat(acmeNode.get("Latitude").asDouble());
        location.setLng(acmeNode.get("Longitude").asDouble());
        String address = acmeNode.get("Address").asText();
        if(!DataHelper.isNullOrEmpty(address)) {
            address = address.trim();
            String postalCode = acmeNode.get("PostalCode").asText();
            if(!DataHelper.isNullOrEmpty(postalCode)) {
                address = address.concat(", ").concat(postalCode);
            }
            location.setAddress(address);
        }
        String city = acmeNode.get("City").asText();
        if(!DataHelper.isNullOrEmpty(city)) {
            location.setCity(city);
        }
        String country = acmeNode.get("Country").asText();
        if(!DataHelper.isNullOrEmpty(country)) {
            location.setCity(country);
        }
        responseItem.setLocation(location);
        String description = acmeNode.get("Description").asText();
        if(!DataHelper.isNullOrEmpty(description)) {
            responseItem.setDescription(description);
        }
        Amenities amenities = new Amenities();
        JsonNode amenitiesNode = acmeNode.get("Facilities");
        List<String> genAmenities = new ArrayList<>();
        if(amenitiesNode != null && amenitiesNode.isArray()) {
            for(JsonNode genAmenityNode : amenitiesNode) {
                genAmenities.add(genAmenityNode.asText().trim());
            }
        }
        amenities.setGeneral(genAmenities);
        responseItem.setAmenities(amenities);
        return responseItem;
    }

    private ResponseItem processPatagoniaData(JsonNode petagoniaNode, ResponseItem responseItem) throws JsonProcessingException {
        String name = petagoniaNode.get("name").asText();
        if(!DataHelper.isNullOrEmpty(name)) {
            if(responseItem.getName() == null) {
                responseItem.setName(name.trim());
            } else {
                String existingName = responseItem.getName();
                responseItem.setName(MergingStrategies.getStringWithMaxLength(existingName, name));
            }
        }
        Location location =  responseItem.getLocation();
        if(location.getLng() == null  || location.getLat() == null) {
            location.setLat(petagoniaNode.get("lat").asDouble());
            location.setLng(petagoniaNode.get("lng").asDouble());
        }
        String address = petagoniaNode.get("address").asText();
        if(!DataHelper.isNullOrEmpty(address)) {
            address = address.trim();
          String existingAddress = responseItem.getLocation().getAddress();
          if(!DataHelper.isNullOrEmpty(existingAddress)) {
              address = MergingStrategies.getStringWithMaxLength(existingAddress, address);
          }
            location.setAddress(address);
        }
        responseItem.setLocation(location);
        String description = petagoniaNode.get("info").asText();
        if(!DataHelper.isNullOrEmpty(description)) {
            if(responseItem.getDescription() != null) {
                String existingDescription = responseItem.getDescription();
                description = MergingStrategies.getDescriptionAfterMerge(existingDescription, description);
            }
            responseItem.setDescription(description);
        }
        Amenities amenities = new Amenities();
        JsonNode amenitiesNode = petagoniaNode.get("amenities");
        List<String> genAmenities = responseItem.getAmenities().getGeneral();
        if(amenitiesNode != null && amenitiesNode.isArray()) {
            for(JsonNode genAmenityNode : amenitiesNode) {
                genAmenities.add(genAmenityNode.asText().trim());
            }
        }
        amenities.setGeneral(genAmenities);
        responseItem.setAmenities(amenities);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode imagesNode = petagoniaNode.get("images");
        Images images = mapper.treeToValue(imagesNode, Images.class);
        responseItem.setImages(images);

        System.out.println("Rooms:");
        for (RoomsItem item : images.getRooms()) {
            System.out.println(item.getDescription() + " → " + item.getLink());
        }

        System.out.println("\nAmenities:");
        for (AmenitiesItem item : images.getAmenities()) {
            System.out.println(item.getDescription() + " → " + item.getLink());
        }
        return responseItem;
    }

    private ResponseItem processPaperfliesData(JsonNode paperfliesNode, ResponseItem responseItem) throws JsonProcessingException {
        String name = paperfliesNode.get("hotel_name").asText();
        if(!DataHelper.isNullOrEmpty(name)) {
            if(responseItem.getName() == null) {
                responseItem.setName(name.trim());
            } else {
                String existingName = responseItem.getName();
                responseItem.setName(MergingStrategies.getStringWithMaxLength(existingName, name));
            }
        }
        Location location =  responseItem.getLocation();
        String address = paperfliesNode.get("address").asText();
        if(!DataHelper.isNullOrEmpty(address)) {
            address = address.trim();
            String existingAddress = responseItem.getLocation().getAddress();
            if(!DataHelper.isNullOrEmpty(existingAddress)) {
                address = MergingStrategies.getStringWithMaxLength(existingAddress, address);
            }
            location.setAddress(address);
        }
        String country = paperfliesNode.get("country").asText();
        if(!DataHelper.isNullOrEmpty(country)) {
            if(location.getCountry() == null) {
                location.setCountry(country);
            }
        }
        responseItem.setLocation(location);
        String description = paperfliesNode.get("details").asText();
        if(!DataHelper.isNullOrEmpty(description)) {
            if(responseItem.getDescription() != null) {
                String existingDescription = responseItem.getDescription();
                description = MergingStrategies.getDescriptionAfterMerge(existingDescription, description);
            }
            responseItem.setDescription(description);
        }
        Amenities amenities = new Amenities();
        JsonNode amenitiesNode = paperfliesNode.get("amenities");
        // TODO --

        responseItem.setAmenities(amenities);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode imagesNode = paperfliesNode.get("Images");
        Images images = mapper.treeToValue(imagesNode, Images.class);
        responseItem.setImages(images);

        System.out.println("Rooms:");
        for (RoomsItem item : images.getRooms()) {
            System.out.println(item.getDescription() + " → " + item.getLink());
        }

        System.out.println("\nAmenities:");
        for (AmenitiesItem item : images.getAmenities()) {
            System.out.println(item.getDescription() + " → " + item.getLink());
        }
        JsonNode booksNode = paperfliesNode.get("booking_conditions");
        List<String> bookCond = new ArrayList<>();
        if(booksNode != null && booksNode.isArray()) {
            for (JsonNode bookingConditionsNode : booksNode) {
                bookCond.add(bookingConditionsNode.asText().trim());
            }
        }
        responseItem.setBookingConditions(bookCond);
        return responseItem;
    }

    private Map<String,Map<String,JsonNode>> buildHotelData(Map<String, JsonNode> dataMap){
        Map<String,Map<String,JsonNode>> hotelDataMap = buildHotelDataMap(dataMap);

        hotelDataMap.forEach((key, hotelData) -> {
            System.out.println("hotel key: " + key);
            Map<String,JsonNode> value = hotelDataMap.get(key);
            value.entrySet().stream().forEach(entry -> {
                System.out.println(entry.getKey() + ": " + entry.getValue().toPrettyString());
            });
        });

        return hotelDataMap;
    }

    /**
     * structure:
     * say for "acme" hotel data: hotelId - abc, destinatonId - 123
     * hotelKey : abc-123
     * hoteldqtaMap -> <hotelkey,<key,hotelobjecy>>
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
                        System.out.println(" not a valid hotelId");
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
                        System.out.println("not valid data");
                    }

                }
            }
        });
                return hotelDataMap;
    }






    private Map<String, JsonNode> getHotelData(List<String> urls){
        Map<String, JsonNode> dataMap = new HashMap<>();
        List<CompletableFuture<Void>> futures = urls.stream()
                .map(url ->
                        CompletableFuture.runAsync(() -> {
                            try (InputStream in = new URL(url).openStream()) {
                                JsonNode currentUpstreamNode = new ObjectMapper().readTree(in);
                                String utlKey = getKey(url);
                                System.out.println(utlKey);
                                dataMap.put(utlKey, currentUpstreamNode);
                                System.out.println("Successfully read: " + url);
                            } catch (Exception e) {
                                System.err.println("Failed to read: " + url);
                                e.printStackTrace();
                            }
                        })).toList();


        // Wait for all to complete (blocking)
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        dataMap.forEach((key, value) -> {
            System.out.println("Key: " + key);
            System.out.println("Value: " + value.toPrettyString());
        });
        return dataMap;
    }

    //TODO - change to enum
    private String getKey(String url) {
        String key = null;
        if(url.contains("acme")){
            key = "acme";
        } else if(url.contains("patagonia")){
            key = "patagonia";
        } else if(url.contains("paperflies")){
            key = "paperflies";
        }

        return key;
    }

//    private String getHotelKey(String urlKey, JsonNode value) {
//
//    }


}
