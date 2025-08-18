package com.ascenda.hotels.service;

import com.ascenda.hotels.integration.DataIntegration;
import com.ascenda.hotels.model.response.Images;
import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.ascenda.hotels.repository.HotelRepository;
import com.ascenda.hotels.repository.entity.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HotelService {

    private final DataIntegration dataIntegration;

    @Autowired
    private HotelRepository hotelRepository;

    public HotelService(DataIntegration dataIntegration) {
        this.dataIntegration = dataIntegration;
    }

    // Fetch all hotels
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Fetch hotel by id
    public Optional<Hotel> getHotelById(String id) {
        return hotelRepository.findById(id);
    }

    // Fetch hotels by destinationId
    public List<Hotel> getHotelsByDestinationId(Long destinationId) {
        return hotelRepository.findByDestinationId(destinationId);
    }


    public String readAndStoreDataFromSrc() {

        try {
            Response response = dataIntegration.processUrls();
            return saveHotelData(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }


    }

    public String saveHotelData(Response response){
        if(response != null && response.getResponse() != null){
            try {
                List<ResponseItem> hotelData = response.getResponse();
                hotelData.forEach(hotel -> {
                    //System.out.println("saving hotel data : "+hotel.toString());
                    Hotel hotelEntity = new Hotel();
                    hotelEntity.setId((hotel.getId() != null)?hotel.getId():"");
                    hotelEntity.setDestinationId((long) ((hotel.getDestinationId() != null) ? hotel.getDestinationId() : 0));
                    hotelEntity.setName((hotel.getName() != null) ? hotel.getName():"");
                    hotelEntity.setDescription((hotel.getDescription() != null) ? hotel.getDescription():"");
                    hotelEntity.setLat((hotel.getLocation() != null && hotel.getLocation().getLat() != null) ? hotel.getLocation().getLat() : null);
                    hotelEntity.setLng((hotel.getLocation() != null && hotel.getLocation().getLng() != null) ? hotel.getLocation().getLng() : null);
                    hotelEntity.setAddress((hotel.getLocation() != null && hotel.getLocation().getAddress() != null) ? hotel.getLocation().getAddress():"");
                    hotelEntity.setCity((hotel.getLocation() != null && hotel.getLocation().getCity() != null) ? hotel.getLocation().getCity():"");
                    hotelEntity.setCountry((hotel.getLocation() != null && hotel.getLocation().getCountry() != null) ? hotel.getLocation().getCountry():"");

                    Map<String, List<String>> amenities = new HashMap<>();
                    if(hotel.getAmenities() != null){
                        amenities.put("general", hotel.getAmenities().getGeneral());
                        amenities.put("room", hotel.getAmenities().getRoom());
                    } else {
                        amenities.put("general", new ArrayList<>());
                        amenities.put("room", new ArrayList<>());
                    }
                    hotelEntity.setAmenities(amenities);

                    Images images = new Images();
                    if(hotel.getImages() != null){
                        images.setSite((hotel.getImages().getSite() != null) ? hotel.getImages().getSite()  : new ArrayList<>());
                        images.setRooms((hotel.getImages().getRooms() != null) ? hotel.getImages().getRooms()  : new ArrayList<>());
                        images.setAmenities((hotel.getImages().getAmenities() != null) ? hotel.getImages().getAmenities()  : new ArrayList<>());
                    }
                    hotelEntity.setImages(images);

                    hotelEntity.setBookingConditions((hotel.getBookingConditions() != null) ? hotel.getBookingConditions()  : new ArrayList<>());

                    hotelRepository.save(hotelEntity);

                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }


        return "";
    }
}
