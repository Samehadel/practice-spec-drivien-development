package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.application.dto.NotificationDto;
import com.example.whatsappqueue.domain.Notification;
import com.example.whatsappqueue.domain.Business;
import com.example.whatsappqueue.domain.QueueEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationMapper Unit Tests")
class NotificationMapperTest {

    private NotificationMapper notificationMapper;
    private Notification testNotification;
    private Business testBusiness;
    private QueueEntry testQueueEntry;

    @BeforeEach
    void setUp() {
        BusinessMapper businessMapper = BusinessMapper.INSTANCE;
        QueueEntryMapper queueEntryMapper = new QueueEntryMapper();
        notificationMapper = new NotificationMapper(businessMapper, queueEntryMapper);
        
        LocalDateTime now = LocalDateTime.now();
        Long businessId = 1L;
        Long queueEntryId = 1L;
        Long notificationId = 1L;
        
        testBusiness = Business.builder()
                .name("Test Restaurant")
                .serviceType("Restaurant")
                .whatsappPhoneNumber("+1234567890")
                .queueOpen(true)
                .averageServiceTimeMinutes(15)
                .notificationThreshold(3)
                .build();
        testBusiness.setId(businessId);
        testBusiness.setCreatedAt(now);
        testBusiness.setUpdatedAt(now);
        
        testQueueEntry = QueueEntry.builder()
                .whatsappIdentifier("customer1@example.com")
                .customerName("John Doe")
                .status(QueueEntry.Status.ACTIVE)
                .joinedAt(now)
                .position(1)
                .build();
        testQueueEntry.setId(queueEntryId);
        testQueueEntry.setCreatedAt(now);
        testQueueEntry.setUpdatedAt(now);
        testQueueEntry.setBusiness(testBusiness);
        
        testNotification = Notification.builder()
                .messageType(Notification.MessageType.JOIN_CONFIRMATION)
                .content("Welcome! You are now in position 1. Estimated wait time: 15 minutes.")
                .status(Notification.Status.SENT)
                .sentAt(now)
                .deliveredAt(now.plusMinutes(1))
                .metadata("{\"whatsapp_message_id\": \"msg_123\"}")
                .build();
        testNotification.setId(notificationId);
        testNotification.setCreatedAt(now);
        testNotification.setUpdatedAt(now);
        testNotification.setBusiness(testBusiness);
        testNotification.setQueueEntry(testQueueEntry);

        NotificationDto testNotificationDto = NotificationDto.builder()
                .messageType(Notification.MessageType.JOIN_CONFIRMATION)
                .content("Welcome! You are now in position 1. Estimated wait time: 15 minutes.")
                .status(Notification.Status.SENT)
                .sentAt(now)
                .deliveredAt(now.plusMinutes(1))
                .metadata("{\"whatsapp_message_id\": \"msg_123\"}")
                .business(businessMapper.toDto(testBusiness))
                .queueEntry(queueEntryMapper.toDto(testQueueEntry))
                .build();
        testNotificationDto.setId(notificationId);
        testNotificationDto.setCreatedAt(now);
        testNotificationDto.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should map Notification entity to NotificationDto")
    void shouldMapEntityToDto() {
        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testNotification.getId());
        assertThat(result.getMessageType()).isEqualTo(testNotification.getMessageType());
        assertThat(result.getContent()).isEqualTo(testNotification.getContent());
        assertThat(result.getStatus()).isEqualTo(testNotification.getStatus());
        assertThat(result.getSentAt()).isEqualTo(testNotification.getSentAt());
        assertThat(result.getDeliveredAt()).isEqualTo(testNotification.getDeliveredAt());
        assertThat(result.getMetadata()).isEqualTo(testNotification.getMetadata());
        assertThat(result.getCreatedAt()).isEqualTo(testNotification.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testNotification.getUpdatedAt());
        assertThat(result.getBusiness()).isNotNull();
        assertThat(result.getBusiness().getName()).isEqualTo(testBusiness.getName());
        assertThat(result.getQueueEntry()).isNotNull();
        assertThat(result.getQueueEntry().getCustomerName()).isEqualTo(testQueueEntry.getCustomerName());
    }

    @Test
    @DisplayName("Should return null when mapping null entity to DTO")
    void shouldReturnNullWhenMappingNullEntityToDto() {
        // When
        NotificationDto result = notificationMapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle entity with null business")
    void shouldHandleEntityWithNullBusiness() {
        // Given
        testNotification.setBusiness(null);

        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusiness()).isNull();
        assertThat(result.getQueueEntry()).isNotNull();
    }

    @Test
    @DisplayName("Should handle entity with null queue entry")
    void shouldHandleEntityWithNullQueueEntry() {
        // Given
        testNotification.setQueueEntry(null);

        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusiness()).isNotNull();
        assertThat(result.getQueueEntry()).isNull();
    }

    @Test
    @DisplayName("Should handle entity with null business and queue entry")
    void shouldHandleEntityWithNullBusinessAndQueueEntry() {
        // Given
        testNotification.setBusiness(null);
        testNotification.setQueueEntry(null);

        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusiness()).isNull();
        assertThat(result.getQueueEntry()).isNull();
    }

    @Test
    @DisplayName("Should map list of Notification entities to list of NotificationDtos")
    void shouldMapEntityListToDtoList() {
        // Given
        List<Notification> entities = List.of(testNotification, createAnotherNotification());

        // When
        List<NotificationDto> result = notificationMapper.toDtoList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMessageType()).isEqualTo(testNotification.getMessageType());
        assertThat(result.get(1).getMessageType()).isEqualTo(Notification.MessageType.POSITION_UPDATE);
    }

    @Test
    @DisplayName("Should handle empty entity list")
    void shouldHandleEmptyEntityList() {
        // Given
        List<Notification> entities = List.of();

        // When
        List<NotificationDto> result = notificationMapper.toDtoList(entities);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle entity with null deliveredAt")
    void shouldHandleEntityWithNullDeliveredAt() {
        // Given
        testNotification.setDeliveredAt(null);

        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeliveredAt()).isNull();
        assertThat(result.getSentAt()).isEqualTo(testNotification.getSentAt());
    }

    @Test
    @DisplayName("Should handle entity with null metadata")
    void shouldHandleEntityWithNullMetadata() {
        // Given
        testNotification.setMetadata(null);

        // When
        NotificationDto result = notificationMapper.toDto(testNotification);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMetadata()).isNull();
    }

    private Notification createAnotherNotification() {
        LocalDateTime now = LocalDateTime.now();
        Notification notification = Notification.builder()
                .messageType(Notification.MessageType.POSITION_UPDATE)
                .content("Your position has changed to 2. Estimated wait time: 30 minutes.")
                .status(Notification.Status.PENDING)
                .sentAt(now)
                .deliveredAt(null)
                .metadata("{\"whatsapp_message_id\": \"msg_456\"}")
                .build();
        notification.setId(2L);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setBusiness(testBusiness);
        notification.setQueueEntry(testQueueEntry);
        return notification;
    }
}
