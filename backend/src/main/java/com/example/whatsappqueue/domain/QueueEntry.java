package com.example.whatsappqueue.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "queue_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QueueEntry extends BaseEntity {

    public enum Status {
        ACTIVE,
        SERVED,
        NO_SHOW,
        LEFT
    }

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

    public long waitTime() {
        if (servedAt != null && joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, servedAt);
        } else if (joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, LocalDateTime.now());
        }
        return 0;
    }

    @Override
    public String toString() {
        return "QueueEntry{" +
                "id=" + getId() +
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
