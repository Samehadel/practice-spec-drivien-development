-- liquibase formatted sql
-- changeset Sameh.Adel:003-create-notifications-table

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    queue_entry_id BIGINT,
    message_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    delivered_at TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE notifications ADD CONSTRAINT fk_notifications_business_id 
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

ALTER TABLE notifications ADD CONSTRAINT fk_notifications_queue_entry_id 
    FOREIGN KEY (queue_entry_id) REFERENCES queue_entries(id) ON DELETE CASCADE;

CREATE INDEX idx_notifications_business_id ON notifications(business_id);
CREATE INDEX idx_notifications_queue_entry_id ON notifications(queue_entry_id);