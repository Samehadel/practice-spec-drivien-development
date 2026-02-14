package com.example.whatsappqueue.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "businesses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Business extends BaseEntity {

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

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QueueEntry> queueEntries;

    @Override
    public String toString() {
        return "Business{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", whatsappPhoneNumber='" + whatsappPhoneNumber + '\'' +
                ", queueOpen=" + queueOpen +
                ", averageServiceTimeMinutes=" + averageServiceTimeMinutes +
                ", notificationThreshold=" + notificationThreshold +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
