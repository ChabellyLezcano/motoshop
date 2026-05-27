-- ============================================================
-- V1: initial schema for Sprint 1
-- Tables: users, motorcycles
-- ============================================================

-- ---------- USERS -------------------------------------------
-- Role is stored as varchar. The Java enum
-- (com.motoshop.api.security.Role) is the single source of truth
-- for the allowed values; Hibernate enforces it on persist.
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    role        VARCHAR(32)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);

-- ---------- CATALOG -----------------------------------------
-- Enriched catalog with technical specs relevant to the buyer.
-- Enumerated columns (engine_type, cooling, category, license)
-- are kept as plain varchar: the Java enums in the catalog package
-- are the single source of truth, so new values do not require an
-- ALTER TABLE migration. Numeric range and non-negativity checks
-- stay in the database, since those are domain invariants that
-- must hold regardless of the application layer.
--
-- Conventions:
--   * price_cents: price in cents (avoids floating point).
--   * fuel_capacity_l: NUMERIC(4,1) for values like 14.5, 17.0.
--   * image_key: object key in MinIO. Null in Sprint 1, populated
--     in Sprint 2.
CREATE TABLE motorcycles (
    id                BIGSERIAL    PRIMARY KEY,
    brand             VARCHAR(100) NOT NULL,
    model             VARCHAR(100) NOT NULL,
    displacement      INTEGER      NOT NULL,
    year              INTEGER      NOT NULL,
    price_cents       BIGINT       NOT NULL,
    stock             INTEGER      NOT NULL DEFAULT 0,
    description       TEXT,
    image_key         VARCHAR(255),

    -- Engine and performance
    power_hp          INTEGER      NOT NULL,
    torque_nm         INTEGER      NOT NULL,
    top_speed_kmh     INTEGER      NOT NULL,
    engine_type       VARCHAR(32)  NOT NULL,
    cooling           VARCHAR(16)  NOT NULL,

    -- Physical
    weight_kg         INTEGER      NOT NULL,
    seat_height_mm    INTEGER      NOT NULL,
    fuel_capacity_l   NUMERIC(4,1) NOT NULL,
    color             VARCHAR(50)  NOT NULL,

    -- Classification
    category          VARCHAR(32)  NOT NULL,
    license           VARCHAR(4)   NOT NULL,

    -- Transmission and electronics
    transmission      INTEGER      NOT NULL,
    abs               BOOLEAN      NOT NULL DEFAULT TRUE,
    traction_control  BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT motorcycles_price_chk        CHECK (price_cents >= 0),
    CONSTRAINT motorcycles_stock_chk        CHECK (stock >= 0),
    CONSTRAINT motorcycles_year_chk         CHECK (year BETWEEN 1900 AND 2100),
    CONSTRAINT motorcycles_power_chk        CHECK (power_hp > 0),
    CONSTRAINT motorcycles_torque_chk       CHECK (torque_nm > 0),
    CONSTRAINT motorcycles_top_speed_chk    CHECK (top_speed_kmh > 0),
    CONSTRAINT motorcycles_weight_chk       CHECK (weight_kg > 0),
    CONSTRAINT motorcycles_seat_chk         CHECK (seat_height_mm > 0),
    CONSTRAINT motorcycles_fuel_chk         CHECK (fuel_capacity_l > 0),
    CONSTRAINT motorcycles_transmission_chk CHECK (transmission BETWEEN 1 AND 8)
);

CREATE INDEX idx_motorcycles_brand          ON motorcycles (brand);
CREATE INDEX idx_motorcycles_brand_model    ON motorcycles (brand, model);
CREATE INDEX idx_motorcycles_category       ON motorcycles (category);
CREATE INDEX idx_motorcycles_license        ON motorcycles (license);