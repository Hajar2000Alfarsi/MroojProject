-- =====================================================================
-- V1__init_schema.sql
-- Initial schema for Mrooj — matches the ERD and JPA entities exactly.
--
-- Place this file at: src/main/resources/db/migration/V1__init_schema.sql
-- (must match spring.flyway.locations=classpath:db/migration)
--
-- Notes:
--   - utf8mb4 is used throughout since the platform serves Arabic content
--     (preferred_language and other Arabic-facing fields) alongside English.
--   - POINT columns use SRID 4326 (WGS84 lat/lng), matching GPS/browser
--     geolocation output and the @Column(columnDefinition = "POINT SRID 4326")
--     annotations on the entities.
--   - FK ON DELETE behavior is a sensible default, not dictated by the ERD —
--     review before going to production. RESTRICT protects against orphaned
--     child rows; SET NULL is used only where the FK itself is nullable.
-- =====================================================================

-- ---------------------------------------------------------------------
-- USERS
-- ---------------------------------------------------------------------
CREATE TABLE users
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    email              VARCHAR(150) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100) NOT NULL,
    phone              VARCHAR(20) NULL,
    role               VARCHAR(20)  NOT NULL,
    enabled            BOOLEAN      NOT NULL DEFAULT TRUE,
    preferred_language VARCHAR(5)   NOT NULL DEFAULT 'ar',
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- FARMERS  (One-to-One with USERS; owning side holds user_id)
-- ---------------------------------------------------------------------
CREATE TABLE farmers
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    farm_name     VARCHAR(150) NOT NULL,
    farm_location POINT        NOT NULL SRID 4326,
    region        VARCHAR(100) NULL,
    farm_size_acres DOUBLE NULL,
    crop_types    VARCHAR(255) NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_farmers_user_id (user_id),
    CONSTRAINT fk_farmers_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- CONSULTANTS  (One-to-One with USERS; owning side holds user_id)
-- ERD requires a SPATIAL INDEX on location for the nearest-expert query.
-- ---------------------------------------------------------------------
CREATE TABLE consultants
(
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    specialty_domain VARCHAR(20) NOT NULL,
    specialty_tags   VARCHAR(255) NULL,
    location         POINT       NOT NULL SRID 4326,
    current_load     INT         NOT NULL DEFAULT 0,
    experience_years INT NULL,
    rating DOUBLE NOT NULL DEFAULT 0,
    available        BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_consultants_user_id (user_id),
    SPATIAL          INDEX idx_consultants_location (location),
    CONSTRAINT fk_consultants_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- BOOKINGS  (many per Farmer; optionally assigned to one Consultant)
-- ---------------------------------------------------------------------
CREATE TABLE bookings
(
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    farmer_id              BIGINT       NOT NULL,
    assigned_consultant_id BIGINT NULL,
    domain                 VARCHAR(20)  NOT NULL,
    subject_type           VARCHAR(100) NOT NULL,
    issue_category         VARCHAR(100) NULL,
    description            TEXT NULL,
    symptoms_image_url     VARCHAR(500) NULL,
    ai_report              JSON NULL,
    location               POINT        NOT NULL SRID 4326,
    consultant_response    TEXT NULL,
    status                 VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY                    idx_bookings_farmer_id (farmer_id),
    KEY                    idx_bookings_assigned_consultant_id (assigned_consultant_id),
    KEY                    idx_bookings_status (status),
    CONSTRAINT fk_bookings_farmer
        FOREIGN KEY (farmer_id) REFERENCES farmers (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_consultant
        FOREIGN KEY (assigned_consultant_id) REFERENCES consultants (id)
            ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- APPOINTMENTS  (tied to a Booking; farmer/consultant denormalized per ERD)
-- ---------------------------------------------------------------------
CREATE TABLE appointments
(
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    booking_id       BIGINT      NOT NULL,
    farmer_id        BIGINT      NOT NULL,
    consultant_id    BIGINT      NOT NULL,
    scheduled_at     DATETIME    NOT NULL,
    duration_minutes INT         NOT NULL DEFAULT 30,
    status           VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    meeting_link     VARCHAR(500) NULL,
    location         VARCHAR(255) NULL,
    notes            TEXT NULL,
    cancellation_reason VARCHAR(500) NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY              idx_appointments_booking_id (booking_id),
    KEY              idx_appointments_farmer_id (farmer_id),
    KEY              idx_appointments_consultant_id (consultant_id),
    CONSTRAINT fk_appointments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appointments_farmer
        FOREIGN KEY (farmer_id) REFERENCES farmers (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appointments_consultant
        FOREIGN KEY (consultant_id) REFERENCES consultants (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- ASSIGNMENT_LOG  (audit trail; no created_at/updated_at per ERD —
-- assigned_at is the meaningful creation moment, set by the app)
-- ---------------------------------------------------------------------
CREATE TABLE assignment_log
(
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    booking_id    BIGINT      NOT NULL,
    consultant_id BIGINT      NOT NULL,
    assigned_at   DATETIME    NOT NULL,
    responded_at  DATETIME NULL,
    outcome       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    KEY           idx_assignment_log_booking_id (booking_id),
    KEY           idx_assignment_log_consultant_id (consultant_id),
    CONSTRAINT fk_assignment_log_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id)
            ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_assignment_log_consultant
        FOREIGN KEY (consultant_id) REFERENCES consultants (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
