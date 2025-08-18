package com.ascenda.hotels.repository;

import com.ascenda.hotels.repository.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, String> {
    List<Hotel> findByDestinationId(Long destinationId);


}
