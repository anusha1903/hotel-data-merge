package com.ascenda.hotels;

import com.ascenda.hotels.integration.DataIntegration;
import com.ascenda.hotels.service.HotelServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * //implements CommandLineRunner -> to run locally wihtout triggering the API
 * //    @Override
 * //    public void run(String... args) {
 * //        hotelService.readAndStoreDataFromSrc();
 * //
 * //        //dataIntegration.processUrls(); // Call your service method here
 * //    }
 */
@SpringBootApplication
@EnableCaching
public class HotelsApplication
{

    public final DataIntegration dataIntegration;

    public final HotelServiceImpl hotelService;

    public HotelsApplication(DataIntegration dataIntegration, HotelServiceImpl hotelService) {
        this.dataIntegration = dataIntegration;
        this.hotelService = hotelService;
    }


    public static void main(String[] args) {
		SpringApplication.run(HotelsApplication.class, args);
	}

//    @Override
//    public void run(String... args) {
//        hotelService.readAndStoreDataFromSrc();
//
//        //dataIntegration.processUrls(); // Call your service method here
//    }


}
