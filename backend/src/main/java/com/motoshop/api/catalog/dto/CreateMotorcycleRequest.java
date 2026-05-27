package com.motoshop.api.catalog.dto;

import java.math.BigDecimal;

import com.motoshop.api.catalog.Category;
import com.motoshop.api.catalog.Cooling;
import com.motoshop.api.catalog.EngineType;
import com.motoshop.api.catalog.LicenseType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateMotorcycleRequest", description = "Payload to create a new motorcycle (ADMIN only)")
public record CreateMotorcycleRequest(
        @NotBlank @Size(max = 100) String brand,
        @NotBlank @Size(max = 100) String model,
        @NotNull @Min(50)  @Max(3000) Integer displacement,
        @NotNull @Min(1900) @Max(2100) Integer year,
        @NotNull @Min(0)   Long priceCents,
        @NotNull @Min(0)   Integer stock,
        @Size(max = 5000) String description,

        @NotNull @Min(1)   @Max(500) Integer powerHp,
        @NotNull @Min(1)   @Max(500) Integer torqueNm,
        @NotNull @Min(1)   @Max(500) Integer topSpeedKmh,
        @NotNull EngineType engineType,
        @NotNull Cooling cooling,

        @NotNull @Min(50)  @Max(500) Integer weightKg,
        @NotNull @Min(500) @Max(1200) Integer seatHeightMm,
        @NotNull @DecimalMin("1.0") @DecimalMax("99.9") BigDecimal fuelCapacityL,
        @NotBlank @Size(max = 50) String color,

        @NotNull Category category,
        @NotNull LicenseType license,
        @NotNull @Min(1)   @Max(8) Integer transmission,
        @NotNull Boolean abs,
        @NotNull Boolean tractionControl
) {
}