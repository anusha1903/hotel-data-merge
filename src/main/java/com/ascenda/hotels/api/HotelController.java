package com.ascenda.hotels.api;

import com.ascenda.hotels.exceptionHandler.DataIngestionException;
import com.ascenda.hotels.exceptionHandler.HotelNotFoundException;
import com.ascenda.hotels.model.request.HotelRequest;
import com.ascenda.hotels.repository.HotelRepository;
import com.ascenda.hotels.repository.entity.Hotel;
import com.ascenda.hotels.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hotel API", description = "Hotel Data APIs")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }


    // GET all hotels
    @GetMapping(("/getAllHotels"))
    @Operation(summary = "Get all the hotels", description = "Fetch all the hotels")
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        if(hotels.isEmpty()){
            throw new HotelNotFoundException("Hotel not found");
        }
        return ResponseEntity.ok(hotels);
    }

    @PostMapping("/getHotelDetails")
    @Operation(summary = "Get hotel by ID or destination_id", description = "Fetch a hotel using its unique ID. One of these 2 attributes would suffice")
    public ResponseEntity<List<Hotel>> getHotelsForReq(@RequestBody HotelRequest request) {

        List<Hotel> results = new ArrayList<>();

        if((request.getId() == null || request.getId().isEmpty()) && (request.getDestinationId() == null)){
            throw new HotelNotFoundException("Missing required parameters");
        }

        // Query by ID if provided
        if (request.getId() != null && !request.getId().isEmpty()) {
            hotelService.getHotelById(request.getId())
                    .ifPresent(results::add);
        } else if (request.getDestinationId() != null) {  // Query by destinationId if provided
            results.addAll(hotelService.getHotelsByDestinationId(request.getDestinationId()));
        }

        if (results.isEmpty()) {
            throw  new HotelNotFoundException("Requested Hotel/Hotels for requested destinationId are not found");
        }

        return ResponseEntity.ok(results);
    }

    // GET all hotels
    @GetMapping(("/ingestHotelData"))
    @Operation(summary = "Read and ingest all the hotel data", description = "stores all hotel data")
    public ResponseEntity<String> saveAllHotels() throws Exception {
        try {
            return ResponseEntity.ok(hotelService.readAndStoreDataFromSrc());
        } catch (DataIngestionException e) {
            throw  new DataIngestionException("Data Ingestion has failed "+e.getMessage());
        } catch (Exception e) {
            throw  new Exception("Data Ingestion has failed "+e.getMessage());
        }

    }
}
