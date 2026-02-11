package com.example.whatsappqueue.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "businesses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "whatsapp_phone_number", nullable = false, unique = true)
    private String whatsappPhoneNumber;

    @Column(name = "queue_open", nullable = false)
    private Boolean queueOpen = true;

    @Column(name = "average_service_time_minutes", nullable = false)
    private Integer averageServiceTimeMinutes = 10;

    @Column(name = "notification_threshold", nullable = false)
    private Integer notificationThreshold = 3;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Business business = (Business) o;
        return id != null && Objects.equals(id, business.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", whatsappPhoneNumber='" + whatsappPhoneNumber + '\'' +
                ", queueOpen=" + queueOpen +
                ", averageServiceTimeMinutes=" + averageServiceTimeMinutes +
                ", notificationThreshold=" + notificationThreshold +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
