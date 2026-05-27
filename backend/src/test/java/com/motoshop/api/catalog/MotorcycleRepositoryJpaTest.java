package com.motoshop.api.catalog;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Persistence-layer tests using H2 and Spring Data's
 * {@code @DataJpaTest} slice. We rely on AUTO-detected H2 since the
 * "test" profile already routes the datasource there.
 *
 * The goal is to lock the JpaSpecifications behaviour: each filter
 * narrows the result set as we expect, and composing them with AND
 * intersects correctly.
 */
@DataJpaTest
@AutoConfigureTestDatabase  // use the configured H2 from application-test.yml
@ActiveProfiles("test")
class MotorcycleRepositoryJpaTest {

    @Autowired MotorcycleRepository repo;

    @BeforeEach
    void seed() {
        repo.deleteAll();
        repo.saveAll(List.of(
                bike("Honda",   "CB650R",   Category.NAKED,      LicenseType.A,  890000L, 5),
                bike("Yamaha",  "MT-07",    Category.NAKED,      LicenseType.A,  790000L, 8),
                bike("KTM",     "390 Duke", Category.NAKED,      LicenseType.A2, 599000L, 0),
                bike("Ducati",  "Panigale", Category.SUPERSPORT, LicenseType.A, 3189000L, 1)
        ));
    }

    @Test
    @DisplayName("Empty filter returns every row")
    void emptyFilter() {
        var spec = MotorcycleSpecifications.allOf();
        assertThat(repo.findAll(spec, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("Brand filter is case-insensitive")
    void brandCaseInsensitive() {
        var spec = MotorcycleSpecifications.allOf(MotorcycleSpecifications.hasBrand("ducati"));
        assertThat(repo.findAll(spec, PageRequest.of(0, 10))).hasSize(1);
    }

    @Test
    @DisplayName("Search hits brand OR model")
    void searchHitsBrandOrModel() {
        var byBrand = MotorcycleSpecifications.allOf(MotorcycleSpecifications.search("yamaha"));
        var byModel = MotorcycleSpecifications.allOf(MotorcycleSpecifications.search("duke"));

        assertThat(repo.findAll(byBrand, PageRequest.of(0, 10))).hasSize(1);
        assertThat(repo.findAll(byModel, PageRequest.of(0, 10))).hasSize(1);
    }

    @Test
    @DisplayName("Composing filters intersects results (AND, not OR)")
    void filtersCombineWithAnd() {
        var spec = MotorcycleSpecifications.allOf(
                MotorcycleSpecifications.hasCategory(Category.NAKED),
                MotorcycleSpecifications.hasLicense(LicenseType.A2)
        );
        assertThat(repo.findAll(spec, PageRequest.of(0, 10)))
                .singleElement()
                .extracting(Motorcycle::getModel).isEqualTo("390 Duke");
    }

    @Test
    @DisplayName("inStock=true hides zero-stock items")
    void inStockExcludesZero() {
        var spec = MotorcycleSpecifications.allOf(MotorcycleSpecifications.inStock(true));
        assertThat(repo.findAll(spec, PageRequest.of(0, 10))).hasSize(3);
    }

    @Test
    @DisplayName("Price range narrows correctly")
    void priceRange() {
        var spec = MotorcycleSpecifications.allOf(
                MotorcycleSpecifications.priceCentsGte(700_000L),
                MotorcycleSpecifications.priceCentsLte(1_000_000L)
        );
        assertThat(repo.findAll(spec, PageRequest.of(0, 10))).hasSize(2);
    }

    // ---- helpers ----

    private static Motorcycle bike(String brand, String model, Category cat,
                                   LicenseType lic, long priceCents, int stock) {
        Motorcycle m = new Motorcycle();
        m.setBrand(brand);
        m.setModel(model);
        m.setDisplacement(650);
        m.setYear(2024);
        m.setPriceCents(priceCents);
        m.setStock(stock);
        m.setPowerHp(70);
        m.setTorqueNm(60);
        m.setTopSpeedKmh(200);
        m.setEngineType(EngineType.PARALLEL_TWIN);
        m.setCooling(Cooling.LIQUID);
        m.setWeightKg(190);
        m.setSeatHeightMm(800);
        m.setFuelCapacityL(new BigDecimal("15.0"));
        m.setColor("Black");
        m.setCategory(cat);
        m.setLicense(lic);
        m.setTransmission(6);
        m.setAbs(true);
        m.setTractionControl(false);
        return m;
    }
}