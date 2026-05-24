package com.sliit.vehiclerentalsystem01.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

    public static final String CUSTOMERS_CACHE   = "customers";
    public static final String VEHICLES_CACHE    = "vehicles";
    public static final String BOOKINGS_CACHE    = "bookings";
    public static final String PAYMENTS_CACHE    = "payments";
    public static final String REVIEWS_CACHE     = "reviews";
    public static final String USERS_CACHE       = "users";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build());
        // Per-cache TTLs via dynamic cache creation
        manager.setCache(Map.of(
                CUSTOMERS_CACHE, Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .build(),
                VEHICLES_CACHE, Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(60, TimeUnit.SECONDS)
                        .build(),
                BOOKINGS_CACHE, Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(15, TimeUnit.SECONDS)
                        .build(),
                PAYMENTS_CACHE, Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(15, TimeUnit.SECONDS)
                        .build(),
                REVIEWS_CACHE, Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .build(),
                USERS_CACHE, Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .build()
        ));
        return manager;
    }
}
