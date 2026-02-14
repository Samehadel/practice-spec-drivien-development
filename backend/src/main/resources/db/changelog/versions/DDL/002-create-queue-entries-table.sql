-- liquibase formatted sql
-- changeset Sameh.Adel:002-create-queue-entries-table

CREATE TABLE queue_entries (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    whatsapp_identifier VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    served_at TIMESTAMP,
    position INTEGER,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE queue_entries ADD CONSTRAINT fk_queue_entries_business_id 
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

CREATE INDEX idx_queue_entries_business_id ON queue_entries(business_id);