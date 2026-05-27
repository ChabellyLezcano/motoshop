package com.motoshop.api.catalog;

import com.motoshop.api.catalog.dto.CreateMotorcycleRequest;
import com.motoshop.api.catalog.dto.MotorcycleResponse;
import com.motoshop.api.catalog.dto.UpdateMotorcycleRequest;
import com.motoshop.api.catalog.model.Motorcycle;

/**
 * Stateless conversions between {@link Motorcycle} entities and their DTOs. Kept as a plain utility
 * class to avoid an unnecessary Spring bean — there is no state and no dependency to inject.
 */
// 1. CAMBIAMOS A PUBLIC
public final class MotorcycleMapper {

  private MotorcycleMapper() {}

  // 2. AÑADIMOS PUBLIC AQUÍ (Este es el que soluciona tu error en el Service)
  public static MotorcycleResponse toResponse(Motorcycle m) {
    return new MotorcycleResponse(
        m.getId(),
        m.getBrand(),
        m.getModel(),
        m.getDisplacement(),
        m.getYear(),
        m.getPriceCents(),
        m.getStock(),
        m.getDescription(),
        m.getImageKey(),
        m.getPowerHp(),
        m.getTorqueNm(),
        m.getTopSpeedKmh(),
        m.getEngineType(),
        m.getCooling(),
        m.getWeightKg(),
        m.getSeatHeightMm(),
        m.getFuelCapacityL(),
        m.getColor(),
        m.getCategory(),
        m.getLicense(),
        m.getTransmission(),
        m.getAbs(),
        m.getTractionControl(),
        m.getCreatedAt(),
        m.getUpdatedAt());
  }

  // 3. AÑADIMOS PUBLIC POR BUENA PRÁCTICA
  public static Motorcycle fromCreate(CreateMotorcycleRequest req) {
    Motorcycle m = new Motorcycle();
    m.setBrand(req.brand());
    m.setModel(req.model());
    m.setDisplacement(req.displacement());
    m.setYear(req.year());
    m.setPriceCents(req.priceCents());
    m.setStock(req.stock());
    m.setDescription(req.description());
    m.setPowerHp(req.powerHp());
    m.setTorqueNm(req.torqueNm());
    m.setTopSpeedKmh(req.topSpeedKmh());
    m.setEngineType(req.engineType());
    m.setCooling(req.cooling());
    m.setWeightKg(req.weightKg());
    m.setSeatHeightMm(req.seatHeightMm());
    m.setFuelCapacityL(req.fuelCapacityL());
    m.setColor(req.color());
    m.setCategory(req.category());
    m.setLicense(req.license());
    m.setTransmission(req.transmission());
    m.setAbs(req.abs());
    m.setTractionControl(req.tractionControl());
    return m;
  }

  // 4. AÑADIMOS PUBLIC AQUÍ TAMBIÉN
  public static void applyUpdate(Motorcycle m, UpdateMotorcycleRequest req) {
    if (req.brand() != null) m.setBrand(req.brand());
    if (req.model() != null) m.setModel(req.model());
    if (req.displacement() != null) m.setDisplacement(req.displacement());
    if (req.year() != null) m.setYear(req.year());
    if (req.priceCents() != null) m.setPriceCents(req.priceCents());
    if (req.stock() != null) m.setStock(req.stock());
    if (req.description() != null) m.setDescription(req.description());
    if (req.powerHp() != null) m.setPowerHp(req.powerHp());
    if (req.torqueNm() != null) m.setTorqueNm(req.torqueNm());
    if (req.topSpeedKmh() != null) m.setTopSpeedKmh(req.topSpeedKmh());
    if (req.engineType() != null) m.setEngineType(req.engineType());
    if (req.cooling() != null) m.setCooling(req.cooling());
    if (req.weightKg() != null) m.setWeightKg(req.weightKg());
    if (req.seatHeightMm() != null) m.setSeatHeightMm(req.seatHeightMm());
    if (req.fuelCapacityL() != null) m.setFuelCapacityL(req.fuelCapacityL());
    if (req.color() != null) m.setColor(req.color());
    if (req.category() != null) m.setCategory(req.category());
    if (req.license() != null) m.setLicense(req.license());
    if (req.transmission() != null) m.setTransmission(req.transmission());
    if (req.abs() != null) m.setAbs(req.abs());
    if (req.tractionControl() != null) m.setTractionControl(req.tractionControl());
  }
}
