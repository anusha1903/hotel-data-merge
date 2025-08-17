package com.ascenda.hotels;

import com.ascenda.hotels.integration.DataIntegration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class HotelsApplication implements CommandLineRunner {

    public final DataIntegration dataIntegration;

    public HotelsApplication(DataIntegration dataIntegration) {
        this.dataIntegration = dataIntegration;
    }


    public static void main(String[] args) {
		SpringApplication.run(HotelsApplication.class, args);
	}

    @Override
    public void run(String... args) {
        dataIntegration.processUrls(); // Call your service method here
    }


}
