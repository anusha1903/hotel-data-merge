package com.ascenda.hotels.api;

import com.ascenda.hotels.exceptionHandler.DataIngestionException;
import com.ascenda.hotels.exceptionHandler.HotelNotFoundException;
import com.ascenda.hotels.integration.DataMerger;
import com.ascenda.hotels.model.request.HotelRequest;
import com.ascenda.hotels.repository.entity.Hotel;
import com.ascenda.hotels.service.HotelServiceImpl;
import com.ascenda.hotels.util.DataHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;


/**
 * This controller has various APIs that allow consumers to consume various hotel data attributes
 * This also allow user to load the data to store into dB
 *
 * @author anusha
 * @since 1.0
 */
@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hotel API", description = "Hotel Data APIs")
public class HotelController {

    Logger logger = LoggerFactory.getLogger(HotelController.class);

    private final HotelServiceImpl hotelService;

    public HotelController(HotelServiceImpl hotelService) {
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
    public ResponseEntity<List<Hotel>> getHotelsForReq(@Valid @RequestBody HotelRequest request) {

        logger.info("Getting hotels for request {}", request);

        List<Hotel> results = new ArrayList<>();

        try {
            boolean isValidaRequest = false;
            try {
                isValidaRequest =  DataHelper.validateUserRequest(request);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            if(isValidaRequest){
                logger.info("valid request");
                // Query by ID if provided
                if (request.getId() != null && !request.getId().isEmpty()) {
                    logger.info("fetching the hotel for id  {}", request.getId());
                    hotelService.getHotelById(request.getId())
                            .ifPresent(results::add);
                } else {  // Query by destinationId if provided
                    logger.info("fetching the hotel for destination id  {}", request.getDestinationId());
                    results.addAll(hotelService.getHotelsByDestinationId(request.getDestinationId()));
                }

                if (results.isEmpty()) {
                    throw  new HotelNotFoundException("Requested Hotel/Hotels for requested destinationId are not found");
                }


            } else {
                logger.error("request validation has failed ");
                throw new IllegalArgumentException("Request validation failed - either hotel ID or destination ID must be provided");
            }
        } catch (HotelNotFoundException e) {
            throw new HotelNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("issue while fetching hotels for requested destinationId", e);
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
