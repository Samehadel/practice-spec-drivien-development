package com.example.whatsappqueue.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    public enum MessageType {
        JOIN_CONFIRMATION,
        POSITION_UPDATE,
        NEARLY_YOUR_TURN,
        YOU_ARE_NEXT,
        LEAVE_CONFIRMATION,
        QUEUE_CLOSED
    }

    public enum Status {
        PENDING,
        SENT,
        DELIVERED,
        FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "queue_entry_id")
    private Long queueEntryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /*
     * We may not need bi-directional relationship, let' keep an eye on it
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    /*
     * We may not need bi-directional relationship, let' keep an eye on it
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_entry_id")
    private QueueEntry queueEntry;

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", businessId=" + businessId +
                ", queueEntryId=" + queueEntryId +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", sentAt=" + sentAt +
                ", deliveredAt=" + deliveredAt +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
