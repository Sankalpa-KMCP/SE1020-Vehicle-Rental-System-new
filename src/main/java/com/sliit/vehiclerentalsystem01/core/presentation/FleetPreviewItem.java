package com.sliit.vehiclerentalsystem01.core.presentation;

/**
 * Precomputed row for the public landing fleet grid (filters, imagery, rate display).
 */
public record FleetPreviewItem(
        String id,
        String brand,
        String model,
        String vehicleType,
        String specificDetail,
        String category,
        String imageUrl,
        long ratePerDay
) {}
