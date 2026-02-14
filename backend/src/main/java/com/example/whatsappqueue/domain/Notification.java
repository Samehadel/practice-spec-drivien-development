package com.example.whatsappqueue.domain;

import java.time.LocalDateTime;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
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

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + getId() +
                ", business=" + business +
                ", queueEntry=" + queueEntry +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", sentAt=" + sentAt +
                ", deliveredAt=" + deliveredAt +
                ", metadata='" + metadata + '\'' +
                '}';
    }
}
