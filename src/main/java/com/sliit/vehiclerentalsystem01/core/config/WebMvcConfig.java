package com.sliit.vehiclerentalsystem01.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.data.directory:data}")
    private String dataDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path avatarDir = Paths.get(dataDirectory).toAbsolutePath().normalize().resolve("avatars");
        String location = avatarDir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(location);

        Path vehicleDir = Paths.get(dataDirectory).toAbsolutePath().normalize().resolve("vehicles");
        String vehicleLocation = vehicleDir.toUri().toString();
        if (!vehicleLocation.endsWith("/")) {
            vehicleLocation = vehicleLocation + "/";
        }
        registry.addResourceHandler("/images/vehicles/**")
                .addResourceLocations(vehicleLocation);
    }
}