package com.ascenda.hotels.service;

import com.ascenda.hotels.integration.DataIntegration;
import com.ascenda.hotels.model.response.Images;
import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.model.response.ResponseItem;
import com.ascenda.hotels.repository.HotelRepository;
import com.ascenda.hotels.repository.entity.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);
    private final DataIntegration dataIntegration;
    private final HotelRepository hotelRepository;

    public HotelServiceImpl(DataIntegration dataIntegration, HotelRepository hotelRepository) {
        this.dataIntegration = dataIntegration;
        this.hotelRepository = hotelRepository;
    }

    // Fetch all hotels
    @Override
    @Cacheable(value = "hotelList", key = "'all'")
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Fetch hotel by id
    @Override
    @Cacheable(value = "hotels", key = "#id")
    public Optional<Hotel> getHotelById(String id) {
        return hotelRepository.findById(id);
    }

    // Fetch hotels by destinationId
    @Override
    @Cacheable(value = "hotelsByDestination", key = "#destinationId")
    public List<Hotel> getHotelsByDestinationId(Long destinationId) {
        return hotelRepository.findByDestinationId(destinationId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String readAndStoreDataFromSrc() {
        logger.info("Starting data ingestion from external sources");
        try {
            Response response = dataIntegration.processUrls();
            String result = saveHotelData(response);
            logger.info("Data ingestion completed successfully");
            return result;
        } catch (Exception e) {
            logger.error("Failed to read and store data from sources", e);
            throw e;
        }
    }

    @Transactional
    @CachePut(value = "hotels", key = "#hotel.id")
    @CacheEvict(value = "hotelList", allEntries = true)
    public String saveHotelData(Response response){
        if(response != null && response.getResponse() != null){
            try {
                List<ResponseItem> hotelData = response.getResponse();
                logger.info("Processing {} hotels for batch save", hotelData.size());
                
                List<Hotel> hotelEntities = hotelData.stream()
                    .map(this::convertResponseItemToHotel)
                    .toList();
                
                hotelRepository.saveAll(hotelEntities);
                logger.info("Successfully saved {} hotels", hotelEntities.size());
                
                return "Data saved successfully. Total hotels: " + hotelEntities.size();
            } catch (Exception ex) {
                logger.error("Failed to save hotel data", ex);
                throw new RuntimeException("Failed to save hotel data", ex);
            }
        }
        return "No data to save";
    }

    private Hotel convertResponseItemToHotel(ResponseItem hotel) {
        Hotel hotelEntity = new Hotel();
        hotelEntity.setId((hotel.getId() != null) ? hotel.getId() : "");
        hotelEntity.setDestinationId((long) ((hotel.getDestinationId() != null) ? hotel.getDestinationId() : 0));
        hotelEntity.setName((hotel.getName() != null) ? hotel.getName() : "");
        hotelEntity.setDescription((hotel.getDescription() != null) ? hotel.getDescription() : "");
        hotelEntity.setLat((hotel.getLocation() != null && hotel.getLocation().getLat() != null) ? hotel.getLocation().getLat() : null);
        hotelEntity.setLng((hotel.getLocation() != null && hotel.getLocation().getLng() != null) ? hotel.getLocation().getLng() : null);
        hotelEntity.setAddress((hotel.getLocation() != null && hotel.getLocation().getAddress() != null) ? hotel.getLocation().getAddress() : "");
        hotelEntity.setCity((hotel.getLocation() != null && hotel.getLocation().getCity() != null) ? hotel.getLocation().getCity() : "");
        hotelEntity.setCountry((hotel.getLocation() != null && hotel.getLocation().getCountry() != null) ? hotel.getLocation().getCountry() : "");

        Map<String, List<String>> amenities = new HashMap<>();
        if (hotel.getAmenities() != null) {
            amenities.put("general", hotel.getAmenities().getGeneral());
            amenities.put("room", hotel.getAmenities().getRoom());
        } else {
            amenities.put("general", new ArrayList<>());
            amenities.put("room", new ArrayList<>());
        }
        hotelEntity.setAmenities(amenities);

        Images images = new Images();
        if (hotel.getImages() != null) {
            images.setSite((hotel.getImages().getSite() != null) ? hotel.getImages().getSite() : new ArrayList<>());
            images.setRooms((hotel.getImages().getRooms() != null) ? hotel.getImages().getRooms() : new ArrayList<>());
            images.setAmenities((hotel.getImages().getAmenities() != null) ? hotel.getImages().getAmenities() : new ArrayList<>());
        }
        hotelEntity.setImages(images);

        hotelEntity.setBookingConditions((hotel.getBookingConditions() != null) ? hotel.getBookingConditions() : new ArrayList<>());

        return hotelEntity;
    }
}
