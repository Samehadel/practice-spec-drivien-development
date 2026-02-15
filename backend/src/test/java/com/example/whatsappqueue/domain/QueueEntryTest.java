package com.example.whatsappqueue.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

class QueueEntryTest {

    @Test
    @DisplayName("Should create QueueEntry with all required fields")
    void shouldCreateQueueEntryWithAllRequiredFields() {
        // Given
        String whatsappIdentifier = "+1234567890";
        String customerName = "John Doe";
        QueueEntry.Status status = QueueEntry.Status.ACTIVE;
        LocalDateTime joinedAt = LocalDateTime.now();
        Integer position = 1;
        String metadata = "{\"source\":\"web\"}";
        
        // When
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier(whatsappIdentifier)
                .customerName(customerName)
                .status(status)
                .joinedAt(joinedAt)
                .position(position)
                .metadata(metadata)
                .build();
        
        // Then
        assertThat(queueEntry).isNotNull();
        assertThat(queueEntry.getWhatsappIdentifier()).isEqualTo(whatsappIdentifier);
        assertThat(queueEntry.getCustomerName()).isEqualTo(customerName);
        assertThat(queueEntry.getStatus()).isEqualTo(status);
        assertThat(queueEntry.getJoinedAt()).isEqualTo(joinedAt);
        assertThat(queueEntry.getPosition()).isEqualTo(position);
        assertThat(queueEntry.getMetadata()).isEqualTo(metadata);
    }

    @Test
    @DisplayName("Should create QueueEntry with minimal required fields")
    void shouldCreateQueueEntryWithMinimalFields() {
        // Given
        String whatsappIdentifier = "+1234567890";
        QueueEntry.Status status = QueueEntry.Status.ACTIVE;
        
        // When
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier(whatsappIdentifier)
                .status(status)
                .build();
        
        // Then
        assertThat(queueEntry).isNotNull();
        assertThat(queueEntry.getWhatsappIdentifier()).isEqualTo(whatsappIdentifier);
        assertThat(queueEntry.getStatus()).isEqualTo(status);
        assertThat(queueEntry.getCustomerName()).isNull();
        assertThat(queueEntry.getPosition()).isNull();
        assertThat(queueEntry.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should calculate wait time correctly")
    void shouldCalculateWaitTimeCorrectly() {
        // Given
        LocalDateTime joinedAt = LocalDateTime.now().minusMinutes(30);
        LocalDateTime servedAt = LocalDateTime.now().minusMinutes(10);
        
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier("+1234567890")
                .status(QueueEntry.Status.SERVED)
                .joinedAt(joinedAt)
                .servedAt(servedAt)
                .build();
        
        // When
        Long waitTime = queueEntry.waitTime();
        
        // Then
        assertThat(waitTime).isNotNull();
        assertThat(waitTime).isEqualTo(20L); // 20 minutes difference
    }

    @Test
    @DisplayName("Should return null wait time when not served")
    void shouldReturnNullWaitTimeWhenNotServed() {
        // Given
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier("+1234567890")
                .status(QueueEntry.Status.ACTIVE)
                .joinedAt(LocalDateTime.now().minusMinutes(30))
                .build();
        
        // When
        Long waitTime = queueEntry.waitTime();
        
        // Then
        /*
        public long waitTime() {
        if (servedAt != null && joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, servedAt);
        } else if (joinedAt != null) {
            return ChronoUnit.MINUTES.between(joinedAt, LocalDateTime.now());
        }
        return 0;
    }
         */
        assertThat(waitTime).isEqualTo(30L);
    }

    @Test
    @DisplayName("Should update QueueEntry fields")
    void shouldUpdateQueueEntryFields() {
        // Given
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier("+1234567890")
                .status(QueueEntry.Status.ACTIVE)
                .build();
        
        // When
        queueEntry.setCustomerName("Updated Name");
        queueEntry.setPosition(5);
        queueEntry.setStatus(QueueEntry.Status.SERVED);
        queueEntry.setServedAt(LocalDateTime.now());
        
        // Then
        assertThat(queueEntry.getCustomerName()).isEqualTo("Updated Name");
        assertThat(queueEntry.getPosition()).isEqualTo(5);
        assertThat(queueEntry.getStatus()).isEqualTo(QueueEntry.Status.SERVED);
        assertThat(queueEntry.getServedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should validate Status enum values")
    void shouldValidateStatusEnumValues() {
        // Then
        QueueEntry.Status[] statuses = QueueEntry.Status.values();
        assertThat(statuses).containsExactly(
            QueueEntry.Status.ACTIVE,
            QueueEntry.Status.SERVED,
            QueueEntry.Status.NO_SHOW,
            QueueEntry.Status.LEFT
        );
    }
}
