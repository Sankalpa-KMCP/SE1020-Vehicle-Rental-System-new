package com.example.vehiclerentalsystem01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VehicleRentalSystemApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(VehicleRentalSystemApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(VehicleRentalSystemApplication.class, args);
    }
}
