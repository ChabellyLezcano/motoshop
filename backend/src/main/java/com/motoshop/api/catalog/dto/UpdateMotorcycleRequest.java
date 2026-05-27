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
import jakarta.validation.constraints.Size;

/**
 * Partial update payload. Every field is optional; only the supplied
 * ones are applied. Validation kicks in only when a field is present,
 * because Bean Validation skips null values on non-@NotNull fields.
 */
@Schema(name = "UpdateMotorcycleRequest",
        description = "Partial update payload. Only the supplied fields are applied.")
public record UpdateMotorcycleRequest(
        @Size(max = 100) String brand,
        @Size(max = 100) String model,
        @Min(50)  @Max(3000) Integer displacement,
        @Min(1900) @Max(2100) Integer year,
        @Min(0)   Long priceCents,
        @Min(0)   Integer stock,
        @Size(max = 5000) String description,

        @Min(1) @Max(500) Integer powerHp,
        @Min(1) @Max(500) Integer torqueNm,
        @Min(1) @Max(500) Integer topSpeedKmh,
        EngineType engineType,
        Cooling cooling,

        @Min(50)  @Max(500) Integer weightKg,
        @Min(500) @Max(1200) Integer seatHeightMm,
        @DecimalMin("1.0") @DecimalMax("99.9") BigDecimal fuelCapacityL,
        @Size(max = 50) String color,

        Category category,
        LicenseType license,
        @Min(1) @Max(8) Integer transmission,
        Boolean abs,
        Boolean tractionControl
) {
}