package com.motoshop.api.catalog.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.motoshop.api.catalog.Category;
import com.motoshop.api.catalog.Cooling;
import com.motoshop.api.catalog.EngineType;
import com.motoshop.api.catalog.LicenseType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Motorcycle", description = "A motorcycle in the catalog")
public record MotorcycleResponse(
        Long id,
        String brand,
        String model,
        Integer displacement,
        Integer year,
        Long priceCents,
        Integer stock,
        String description,
        String imageKey,
        Integer powerHp,
        Integer torqueNm,
        Integer topSpeedKmh,
        EngineType engineType,
        Cooling cooling,
        Integer weightKg,
        Integer seatHeightMm,
        BigDecimal fuelCapacityL,
        String color,
        Category category,
        LicenseType license,
        Integer transmission,
        Boolean abs,
        Boolean tractionControl,
        Instant createdAt,
        Instant updatedAt
) {
}