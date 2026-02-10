package com.example.whatsappqueue.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "queue_state_changes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueStateChange {

    public enum ChangeType {
        CUSTOMER_JOINED,
        CUSTOMER_LEFT,
        CUSTOMER_SERVED,
        CUSTOMER_NO_SHOW,
        QUEUE_ADVANCED,
        QUEUE_OPENED,
        QUEUE_CLOSED
    }

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "queue_entry_id")
    private UUID queueEntryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @Column(name = "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueStateChange that = (QueueStateChange) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "QueueStateChange{" +
                "id=" + id +
                ", businessId=" + businessId +
                ", queueEntryId=" + queueEntryId +
                ", changeType=" + changeType +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
