package com.motoshop.api.catalog.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "motorcycles")
public class Motorcycle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String brand;

  @Column(nullable = false, length = 100)
  private String model;

  @Column(nullable = false)
  private Integer displacement;

  @Column(name = "`year`", nullable = false)
  private Integer year;

  @Column(name = "price_cents", nullable = false)
  private Long priceCents;

  @Column(nullable = false)
  private Integer stock;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "image_key", length = 255)
  private String imageKey;

  @Column(name = "power_hp", nullable = false)
  private Integer powerHp;

  @Column(name = "torque_nm", nullable = false)
  private Integer torqueNm;

  @Column(name = "top_speed_kmh", nullable = false)
  private Integer topSpeedKmh;

  @Enumerated(EnumType.STRING)
  @Column(name = "engine_type", nullable = false, length = 32)
  private EngineType engineType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private Cooling cooling;

  @Column(name = "weight_kg", nullable = false)
  private Integer weightKg;

  @Column(name = "seat_height_mm", nullable = false)
  private Integer seatHeightMm;

  @Column(name = "fuel_capacity_l", nullable = false, precision = 4, scale = 1)
  private BigDecimal fuelCapacityL;

  @Column(nullable = false, length = 50)
  private String color;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private Category category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 4)
  private LicenseType license;

  @Column(nullable = false)
  private Integer transmission;

  @Column(nullable = false)
  private Boolean abs;

  @Column(name = "traction_control", nullable = false)
  private Boolean tractionControl;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public Motorcycle() {}

  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getBrand() {
    return brand;
  }

  public String getModel() {
    return model;
  }

  public Integer getDisplacement() {
    return displacement;
  }

  public Integer getYear() {
    return year;
  }

  public Long getPriceCents() {
    return priceCents;
  }

  public Integer getStock() {
    return stock;
  }

  public String getDescription() {
    return description;
  }

  public String getImageKey() {
    return imageKey;
  }

  public Integer getPowerHp() {
    return powerHp;
  }

  public Integer getTorqueNm() {
    return torqueNm;
  }

  public Integer getTopSpeedKmh() {
    return topSpeedKmh;
  }

  public EngineType getEngineType() {
    return engineType;
  }

  public Cooling getCooling() {
    return cooling;
  }

  public Integer getWeightKg() {
    return weightKg;
  }

  public Integer getSeatHeightMm() {
    return seatHeightMm;
  }

  public BigDecimal getFuelCapacityL() {
    return fuelCapacityL;
  }

  public String getColor() {
    return color;
  }

  public Category getCategory() {
    return category;
  }

  public LicenseType getLicense() {
    return license;
  }

  public Integer getTransmission() {
    return transmission;
  }

  public Boolean getAbs() {
    return abs;
  }

  public Boolean getTractionControl() {
    return tractionControl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public void setDisplacement(Integer displacement) {
    this.displacement = displacement;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public void setPriceCents(Long priceCents) {
    this.priceCents = priceCents;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImageKey(String imageKey) {
    this.imageKey = imageKey;
  }

  public void setPowerHp(Integer powerHp) {
    this.powerHp = powerHp;
  }

  public void setTorqueNm(Integer torqueNm) {
    this.torqueNm = torqueNm;
  }

  public void setTopSpeedKmh(Integer topSpeedKmh) {
    this.topSpeedKmh = topSpeedKmh;
  }

  public void setEngineType(EngineType engineType) {
    this.engineType = engineType;
  }

  public void setCooling(Cooling cooling) {
    this.cooling = cooling;
  }

  public void setWeightKg(Integer weightKg) {
    this.weightKg = weightKg;
  }

  public void setSeatHeightMm(Integer seatHeightMm) {
    this.seatHeightMm = seatHeightMm;
  }

  public void setFuelCapacityL(BigDecimal fuelCapacityL) {
    this.fuelCapacityL = fuelCapacityL;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setLicense(LicenseType license) {
    this.license = license;
  }

  public void setTransmission(Integer transmission) {
    this.transmission = transmission;
  }

  public void setAbs(Boolean abs) {
    this.abs = abs;
  }

  public void setTractionControl(Boolean tractionControl) {
    this.tractionControl = tractionControl;
  }
}