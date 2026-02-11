-- liquibase formatted sql
-- changeset Sameh.Adel:004-create-queue-state-changes-table

CREATE TABLE queue_state_changes (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    queue_entry_id BIGINT,
    change_type VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    metadata JSONB
);

ALTER TABLE queue_state_changes ADD CONSTRAINT fk_queue_state_changes_business_id 
    FOREIGN KEY (business_id) REFERENCES businesses(id) ON DELETE CASCADE;

ALTER TABLE queue_state_changes ADD CONSTRAINT fk_queue_state_changes_queue_entry_id 
    FOREIGN KEY (queue_entry_id) REFERENCES queue_entries(id) ON DELETE SET NULL;

CREATE INDEX idx_queue_state_changes_business_id ON queue_state_changes(business_id);
CREATE INDEX idx_queue_state_changes_created_at ON queue_state_changes(created_at);
CREATE INDEX idx_queue_state_changes_type ON queue_state_changes(change_type);
