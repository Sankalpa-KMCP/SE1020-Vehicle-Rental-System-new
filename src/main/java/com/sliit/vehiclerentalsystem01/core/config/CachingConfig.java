package com.sliit.vehiclerentalsystem01.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CachingConfig {

    public static final String CUSTOMERS_CACHE = "customers";
    public static final String VEHICLES_CACHE = "vehicles";
    public static final String BOOKINGS_CACHE = "bookings";
    public static final String PAYMENTS_CACHE = "payments";
    public static final String REVIEWS_CACHE = "reviews";
    public static final String USERS_CACHE = "users";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                CUSTOMERS_CACHE, VEHICLES_CACHE, BOOKINGS_CACHE,
                PAYMENTS_CACHE, REVIEWS_CACHE, USERS_CACHE
        );
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(30, TimeUnit.SECONDS));
        return manager;
    }
}
