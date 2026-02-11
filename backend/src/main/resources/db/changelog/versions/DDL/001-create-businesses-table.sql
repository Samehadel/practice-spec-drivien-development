-- liquibase formatted sql
-- changeset Sameh.Adel:001-create-businesses-table

CREATE TABLE businesses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    service_type VARCHAR(100) NOT NULL,
    whatsapp_phone_number VARCHAR(20) NOT NULL UNIQUE,
    queue_open BOOLEAN DEFAULT true NOT NULL,
    average_service_time_minutes INTEGER DEFAULT 10 NOT NULL,
    notification_threshold INTEGER DEFAULT 3 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_businesses_whatsapp_phone_number ON businesses(whatsapp_phone_number);

ALTER TABLE businesses ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
