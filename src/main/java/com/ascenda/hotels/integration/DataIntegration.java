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
import java.util.stream.Collectors;

@Service
public class DataIntegration {

    private final UrlConfig urlConfig;

    public DataIntegration(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }

    //Reading data from upstreams/sources
    // Here the aim is not to create the pojo from json. So jsonnode is used instead of jsonobject

    public void processUrls(){

        System.out.println("read the urls");
        List<String> urls = urlConfig.getUrlList();

        System.out.println("read the data from urls");
        Map<String, JsonNode> dataMap = getHotelData(urls);

        // take each upstream data and create a map with hotelId as the key
        //assuminh hotelId would be unique

        System.out.println("get response for urls");

      Response  hotelDataResponse = getMergedHotelData(dataMap);



    }

    private Response getMergedHotelData(Map<String, JsonNode> dataMap){
        System.out.println("rebuild the hotel data");
        Map<String,Map<String,JsonNode>> hotelData =  buildHotelData(dataMap);
        System.out.println("process the data");
        // responseitem -> holds merged of one hotel data from all the upstreams
        // list<responseitem> -> response -> holds all the merged hotels data
        Response response = new Response();
        List<ResponseItem> responseItems = new ArrayList<>();
        hotelData.forEach((key, eachHotelData) -> {
            System.out.println("hotel key: " + key);
            Map<String,JsonNode> value = hotelData.get(key);
            ResponseItem responseItem = getProcessedHotelData(value);
            ObjectMapper mapper = new ObjectMapper();

            // Convert the POJO to a JsonNode
            JsonNode jsonNode = mapper.valueToTree(responseItem);

            // Print the JsonNode (which will implicitly call its toString() method)
            System.out.println(jsonNode);
            responseItems.add(responseItem);
        });
        response.setResponse(responseItems);
        return response;
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

        if (eachHotelDataFromAllSources.containsKey("acme")) {
            System.out.println("reading acme");
            try {
                JsonNode acmeNode = eachHotelDataFromAllSources.get("acme");
                if(acmeNode != null){
                    processAcmeData(acmeNode, responseItem);
                }
            } catch (Exception e) {
                System.out.println("exception in acme"+e.getMessage());
                throw new RuntimeException(e);
            }
        }

        if (eachHotelDataFromAllSources.containsKey("patagonia")) {
            try {
                System.out.println("reading patagonia");
                JsonNode patagoniaNode = eachHotelDataFromAllSources.get("patagonia");
                if(patagoniaNode != null) {
                    processPatagoniaData(patagoniaNode, responseItem);
                }

            } catch (Exception e) {
                System.out.println("exception in patagonia"+e.getMessage());
                throw new RuntimeException(e);
            }
        }

        if (eachHotelDataFromAllSources.containsKey("paperflies")) {
            try {
                System.out.println("reading paperflies");
                JsonNode paperfliesNode = eachHotelDataFromAllSources.get("paperflies");
                if(paperfliesNode != null) {
                    processPaperfliesData(paperfliesNode, responseItem);
                }

            } catch (Exception e) {
                System.out.println("exception in paperflies"+e.getMessage());
                throw new RuntimeException(e);
            }
        }

        //System.out.println("responseItem for hotelId : " + responseItem);

        return  responseItem;
    }

    private ResponseItem processAcmeData(JsonNode acmeNode, ResponseItem responseItem){
        responseItem.setId(acmeNode.get("Id").asText().trim());
        int destId = acmeNode.get("DestinationId").asInt();
        if(destId > 0) {
            responseItem.setDestinationId(destId);
        }

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
        double latitude = acmeNode.get("Latitude").asDouble();
        double longitude = acmeNode.get("Longitude").asDouble();
        if(latitude > 0d && longitude > 0d) {
            location.setLat(latitude);
            location.setLng(longitude);
        }

        String address = acmeNode.get("Address").asText();
        if(!DataHelper.isNullOrEmpty(address)) {
            address = address.trim();
            String postalCode = acmeNode.get("PostalCode").asText();
            if(!DataHelper.isNullOrEmpty(postalCode)) {
                if(!address.contains(postalCode)) {
                    address = address.concat(", ").concat(postalCode);
                }
            }
            location.setAddress(address);
        }
        String city = acmeNode.get("City").asText();
        if(!DataHelper.isNullOrEmpty(city)) {
            location.setCity(city.trim());
        }
        String country = acmeNode.get("Country").asText();
        if(!DataHelper.isNullOrEmpty(country)) {
            location.setCountry(country);
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
                genAmenities.add(genAmenityNode.asText().trim().toLowerCase());
            }
        }
        amenities.setGeneral(genAmenities);
        responseItem.setAmenities(amenities);
        return responseItem;
    }

    private ResponseItem processPatagoniaData(JsonNode petagoniaNode, ResponseItem responseItem) throws JsonProcessingException {
        if(DataHelper.isNullOrEmpty(responseItem.getId())) {
            responseItem.setId(petagoniaNode.get("id").asText().trim());
        }
        int destId = petagoniaNode.get("destination").asInt();
        if(destId > 0 && responseItem.getDestinationId() == null) {
            responseItem.setDestinationId(destId);
        }
        String name = petagoniaNode.get("name").asText();
        if(!DataHelper.isNullOrEmpty(name)) {
            if(responseItem.getName() == null) {
                responseItem.setName(name.trim());
            } else {
                String existingName = responseItem.getName();
                responseItem.setName(MergingStrategies.getStringWithMaxLength(existingName, name));
            }
        }
        Location location = null;
        if(responseItem.getLocation() != null){
            location =  responseItem.getLocation();
        } else {
            location = new Location();
        }
        if(location.getLng() == null  || location.getLat() == null || location.getLng() == 0d || location.getLat() == 0d) {
            double latitude = petagoniaNode.get("lat").asDouble();
            double longitude = petagoniaNode.get("lng").asDouble();
            if(latitude > 0d && longitude > 0d) {
                location.setLat(latitude);
                location.setLng(longitude);
            }
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
        List<String> genAmenities = null;
        if(responseItem.getAmenities() != null) {
            genAmenities = responseItem.getAmenities().getGeneral();
        } else {
            genAmenities = new ArrayList<>();
        }

        if(amenitiesNode != null && amenitiesNode.isArray()) {
            for(JsonNode genAmenityNode : amenitiesNode) {
                genAmenities.add(genAmenityNode.asText().trim().toLowerCase());
            }
        }
        amenities.setGeneral(genAmenities);
        responseItem.setAmenities(amenities);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode imagesNode = petagoniaNode.get("images");
        if(imagesNode != null) {
            Images images = mapper.treeToValue(imagesNode, Images.class);
            responseItem.setImages(images);
        }


//        System.out.println("Rooms:");
//        for (RoomsItem item : images.getRooms()) {
//            System.out.println(item.getDescription() + " → " + item.getLink());
//        }

//        System.out.println("\nAmenities:");
//        for (AmenitiesItem item : images.getAmenities()) {
//            System.out.println(item.getDescription() + " → " + item.getLink());
//        }
        return responseItem;
    }

    private ResponseItem processPaperfliesData(JsonNode paperfliesNode, ResponseItem responseItem) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        if(DataHelper.isNullOrEmpty(responseItem.getId())) {
            responseItem.setId(paperfliesNode.get("hotel_id").asText().trim());
        }
        int destId = paperfliesNode.get("destination_id").asInt();
        if(destId > 0 && responseItem.getDestinationId() == null) {
            responseItem.setDestinationId(destId);
        }
        String name = paperfliesNode.get("hotel_name").asText();
        if(!DataHelper.isNullOrEmpty(name)) {
            if(responseItem.getName() == null) {
                responseItem.setName(name.trim());
            } else {
                String existingName = responseItem.getName();
                responseItem.setName(MergingStrategies.getStringWithMaxLength(existingName, name));
            }
        }
        Location location =  null;
        if(responseItem.getLocation() != null) {
            location =  responseItem.getLocation();
        } else {
            location = new Location();
        }
        JsonNode locationNode = paperfliesNode.get("location");
        Location currentLocation = mapper.treeToValue(locationNode, Location.class);
        String address = currentLocation.getAddress();
        if(!DataHelper.isNullOrEmpty(address)) {
            address = address.trim();
            String existingAddress = responseItem.getLocation().getAddress();
            if(!DataHelper.isNullOrEmpty(existingAddress)) {
                address = MergingStrategies.getStringWithMaxLength(existingAddress, address);
            }
            location.setAddress(address);
        }
        String country = currentLocation.getCountry();
        if(!DataHelper.isNullOrEmpty(country)) {
            if(location.getCountry() == null) {
                location.setCountry(country.trim());
            } else {
                location.setCountry(MergingStrategies.getStringWithMaxLength(location.getCountry(), country));
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

        JsonNode amenitiesNode = paperfliesNode.get("amenities");
        List<String> genAmenities = null;
        if(responseItem.getAmenities() != null) {
            genAmenities = responseItem.getAmenities().getGeneral();
        } else {
            genAmenities = new ArrayList<>();
        }
        Amenities amenities = mapper.treeToValue(amenitiesNode, Amenities.class);
        genAmenities.addAll(amenities.getGeneral());
        Map<String, List<String>> mergedAmenities = MergingStrategies.cleanAmenities(genAmenities, amenities.getRoom());
        amenities.setGeneral(mergedAmenities.get("general"));
        amenities.setRoom(mergedAmenities.get("room"));
        responseItem.setAmenities(amenities);

        // check and get existing image object
        Images currentImage = null;
        if(responseItem.getImages() != null) {
            currentImage = responseItem.getImages();
        } else {
            currentImage = new Images();
        }

        // get the image object from source
        JsonNode imagesNode = paperfliesNode.get("images");
        Images images = mapper.treeToValue(imagesNode, Images.class);
        if(images!= null && images.getRooms() != null) { //chk if src img obj is not null
            List<RoomsItem> currentRooms = images.getRooms();
            if(responseItem.getImages() != null && responseItem.getImages().getRooms() != null) {
                // get the existing rooms in the current response Img obj
                List<RoomsItem> existingRooms = responseItem.getImages().getRooms();
                //get rooms from current image node

                List<RoomsItem> unMatched = existingRooms.stream()
                        .filter(r1 -> currentRooms.stream()
                                .noneMatch(r2 -> r1.getLink().equals(r2.getLink())))
                        .collect(Collectors.toList());
                currentRooms.addAll(unMatched);
//                System.out.println("Rooms:");
//                for (RoomsItem item : images.getRooms()) {
//                    System.out.println(item.getDescription() + " → " + item.getLink());
//                }
            }
            currentImage.setRooms(currentRooms);
        }


        if(images!= null && images.getSite() != null) {
            List<SiteItem> currentSite = images.getSite();
            if(responseItem.getImages() != null && responseItem.getImages().getSite() != null) {
                List<SiteItem> existingSite = responseItem.getImages().getSite();
                List<SiteItem> unMatchedSite = existingSite.stream()
                        .filter(r1 -> currentSite.stream()
                                .noneMatch(r2 -> r1.getLink().equals(r2.getLink())))
                        .toList();

                currentSite.addAll(unMatchedSite);
//                System.out.println("\nAmenities:");
//                for (AmenitiesItem item : images.getAmenities()) {
//                    System.out.println(item.getDescription() + " → " + item.getLink());
//                }
            }
            currentImage.setSite(currentSite);
        }
        responseItem.setImages(currentImage);


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

//        dataMap.forEach((key, value) -> {
//            System.out.println("Key: " + key);
//            System.out.println("Value: " + value.toPrettyString());
//        });
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
