package com.ascenda.hotels.service;

import com.ascenda.hotels.model.response.Response;
import com.ascenda.hotels.repository.entity.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    public List<Hotel> getAllHotels();
    public Optional<Hotel> getHotelById(String id);
    public List<Hotel> getHotelsByDestinationId(Long destinationId);
    public String readAndStoreDataFromSrc();
}
