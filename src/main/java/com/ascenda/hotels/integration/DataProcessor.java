package com.ascenda.hotels.integration;

import com.ascenda.hotels.model.response.*;
import com.ascenda.hotels.util.DataHelper;
import com.ascenda.hotels.util.MergingStrategies;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataProcessor {

    public ResponseItem processAcmeData(JsonNode acmeNode, ResponseItem responseItem){
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

    public ResponseItem processPatagoniaData(JsonNode petagoniaNode, ResponseItem responseItem) throws JsonProcessingException {
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

    public ResponseItem processPaperfliesData(JsonNode paperfliesNode, ResponseItem responseItem) throws JsonProcessingException {
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
}
