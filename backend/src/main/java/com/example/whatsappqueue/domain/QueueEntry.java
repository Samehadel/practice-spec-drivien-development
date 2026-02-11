package com.example.whatsappqueue.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "queue_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntry {

    public enum Status {
        ACTIVE,
        SERVED,
        NO_SHOW,
        LEFT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "whatsapp_identifier", nullable = false)
    private String whatsappIdentifier;

    @Column(name = "customer_name")
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    @Column(name = "position")
    private Integer position;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /*
     * We may not need bi-directional relationship, let' keep an eye on it
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public long calculateWaitTime() {
        if (servedAt != null && joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, servedAt);
        } else if (joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, LocalDateTime.now());
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueEntry that = (QueueEntry) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "QueueEntry{" +
                "id=" + id +
                ", business=" + business +
                ", whatsappIdentifier='" + whatsappIdentifier + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", joinedAt=" + joinedAt +
                ", servedAt=" + servedAt +
                ", position=" + position +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
