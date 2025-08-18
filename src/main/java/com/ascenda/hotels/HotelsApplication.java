package com.ascenda.hotels;

import com.ascenda.hotels.integration.DataIntegration;
import com.ascenda.hotels.repository.HotelRepository;
import com.ascenda.hotels.service.HotelService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class HotelsApplication //implements CommandLineRunner
{

    public final DataIntegration dataIntegration;

    public final HotelService hotelService;

    public HotelsApplication(DataIntegration dataIntegration, HotelService hotelService) {
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
