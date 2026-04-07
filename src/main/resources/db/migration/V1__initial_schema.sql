-- V1__initial_schema.sql
CREATE TABLE IF NOT EXISTS location (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS department (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    location_id BIGINT NOT NULL REFERENCES location(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, location_id)
);

CREATE TABLE IF NOT EXISTS category (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(500),
    department_id BIGINT NOT NULL REFERENCES department(id) ON DELETE CASCADE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, department_id)
);

CREATE TABLE IF NOT EXISTS subcategory (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, category_id)
);

CREATE TABLE IF NOT EXISTS sku (
    id             BIGSERIAL PRIMARY KEY,
    sku_code       VARCHAR(50) NOT NULL UNIQUE,
    name           VARCHAR(255) NOT NULL,
    location_id    BIGINT REFERENCES location(id),
    department_id  BIGINT REFERENCES department(id),
    category_id    BIGINT REFERENCES category(id),
    subcategory_id BIGINT REFERENCES subcategory(id),
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_user (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL DEFAULT 'USER',
    enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_department_location ON department(location_id);
CREATE INDEX idx_category_department ON category(department_id);
CREATE INDEX idx_subcategory_category ON subcategory(category_id);
CREATE INDEX idx_sku_location ON sku(location_id);
CREATE INDEX idx_sku_department ON sku(department_id);
CREATE INDEX idx_sku_category ON sku(category_id);
CREATE INDEX idx_sku_subcategory ON sku(subcategory_id);
