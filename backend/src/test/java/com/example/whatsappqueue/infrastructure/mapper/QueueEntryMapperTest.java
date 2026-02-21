package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.domain.QueueEntry;
import com.example.whatsappqueue.domain.Business;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("QueueEntryMapper Unit Tests")
class QueueEntryMapperTest {

    private QueueEntryMapper queueEntryMapper;
    private QueueEntry testQueueEntry;
    private QueueEntryDto testQueueEntryDto;
    private Business testBusiness;

    @BeforeEach
    void setUp() {
        queueEntryMapper = new QueueEntryMapper();
        
        LocalDateTime now = LocalDateTime.now();
        Long businessId = 1L;
        Long queueEntryId = 1L;
        
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
                .servedAt(null)
                .position(1)
                .metadata("{\"source\": \"whatsapp\"}")
                .build();
        testQueueEntry.setId(queueEntryId);
        testQueueEntry.setCreatedAt(now);
        testQueueEntry.setUpdatedAt(now);
        testQueueEntry.setBusiness(testBusiness);
        
        testQueueEntryDto = QueueEntryDto.builder()
                .whatsappIdentifier("customer1@example.com")
                .customerName("John Doe")
                .status(QueueEntry.Status.ACTIVE)
                .joinedAt(now)
                .servedAt(null)
                .position(1)
                .metadata("{\"source\": \"whatsapp\"}")
                .business(BusinessMapper.INSTANCE.toDto(testBusiness))
                .waitTime(15L)
                .build();
        testQueueEntryDto.setId(queueEntryId);
        testQueueEntryDto.setCreatedAt(now);
        testQueueEntryDto.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should map QueueEntry entity to QueueEntryDto")
    void shouldMapEntityToDto() {
        // When
        QueueEntryDto result = queueEntryMapper.toDto(testQueueEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testQueueEntry.getId());
        assertThat(result.getWhatsappIdentifier()).isEqualTo(testQueueEntry.getWhatsappIdentifier());
        assertThat(result.getCustomerName()).isEqualTo(testQueueEntry.getCustomerName());
        assertThat(result.getStatus()).isEqualTo(testQueueEntry.getStatus());
        assertThat(result.getJoinedAt()).isEqualTo(testQueueEntry.getJoinedAt());
        assertThat(result.getServedAt()).isEqualTo(testQueueEntry.getServedAt());
        assertThat(result.getPosition()).isEqualTo(testQueueEntry.getPosition());
        assertThat(result.getMetadata()).isEqualTo(testQueueEntry.getMetadata());
        assertThat(result.getCreatedAt()).isEqualTo(testQueueEntry.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testQueueEntry.getUpdatedAt());
        assertThat(result.getBusiness()).isNotNull();
        assertThat(result.getBusiness().getName()).isEqualTo(testBusiness.getName());
        assertThat(result.getWaitTime()).isNotNull();
    }

    @Test
    @DisplayName("Should map QueueEntryDto to QueueEntry entity")
    void shouldMapDtoToEntity() {
        // When
        QueueEntry result = queueEntryMapper.toEntity(testQueueEntryDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testQueueEntryDto.getId());
        assertThat(result.getWhatsappIdentifier()).isEqualTo(testQueueEntryDto.getWhatsappIdentifier());
        assertThat(result.getCustomerName()).isEqualTo(testQueueEntryDto.getCustomerName());
        assertThat(result.getStatus()).isEqualTo(testQueueEntryDto.getStatus());
        assertThat(result.getJoinedAt()).isEqualTo(testQueueEntryDto.getJoinedAt());
        assertThat(result.getServedAt()).isEqualTo(testQueueEntryDto.getServedAt());
        assertThat(result.getPosition()).isEqualTo(testQueueEntryDto.getPosition());
        assertThat(result.getMetadata()).isEqualTo(testQueueEntryDto.getMetadata());
        assertThat(result.getCreatedAt()).isEqualTo(testQueueEntryDto.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testQueueEntryDto.getUpdatedAt());
        assertThat(result.getBusiness()).isNull(); // Entity mapping doesn't include business relationship
    }

    @Test
    @DisplayName("Should return null when mapping null entity to DTO")
    void shouldReturnNullWhenMappingNullEntityToDto() {
        // When
        QueueEntryDto result = queueEntryMapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when mapping null DTO to entity")
    void shouldReturnNullWhenMappingNullDtoToEntity() {
        // When
        QueueEntry result = queueEntryMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle entity with null business")
    void shouldHandleEntityWithNullBusiness() {
        // Given
        testQueueEntry.setBusiness(null);

        // When
        QueueEntryDto result = queueEntryMapper.toDto(testQueueEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBusiness()).isNull();
    }

    @Test
    @DisplayName("Should map list of QueueEntry entities to list of QueueEntryDtos")
    void shouldMapEntityListToDtoList() {
        // Given
        List<QueueEntry> entities = List.of(testQueueEntry, createAnotherQueueEntry());

        // When
        List<QueueEntryDto> result = queueEntryMapper.toDtoList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCustomerName()).isEqualTo(testQueueEntry.getCustomerName());
        assertThat(result.get(1).getCustomerName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should map list of QueueEntryDtos to list of QueueEntry entities")
    void shouldMapDtoListToEntityList() {
        // Given
        List<QueueEntryDto> dtos = List.of(testQueueEntryDto, createAnotherQueueEntryDto());

        // When
        List<QueueEntry> result = queueEntryMapper.toEntityList(dtos);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCustomerName()).isEqualTo(testQueueEntryDto.getCustomerName());
        assertThat(result.get(1).getCustomerName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should handle empty entity list")
    void shouldHandleEmptyEntityList() {
        // Given
        List<QueueEntry> entities = List.of();

        // When
        List<QueueEntryDto> result = queueEntryMapper.toDtoList(entities);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty DTO list")
    void shouldHandleEmptyDtoList() {
        // Given
        List<QueueEntryDto> dtos = List.of();

        // When
        List<QueueEntry> result = queueEntryMapper.toEntityList(dtos);

        // Then
        assertThat(result).isEmpty();
    }

    private QueueEntry createAnotherQueueEntry() {
        LocalDateTime now = LocalDateTime.now();
        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier("customer2@example.com")
                .customerName("Jane Smith")
                .status(QueueEntry.Status.ACTIVE)
                .joinedAt(now)
                .servedAt(null)
                .position(2)
                .metadata("{\"source\": \"whatsapp\"}")
                .build();
        queueEntry.setId(2L);
        queueEntry.setCreatedAt(now);
        queueEntry.setUpdatedAt(now);
        queueEntry.setBusiness(testBusiness);
        return queueEntry;
    }

    private QueueEntryDto createAnotherQueueEntryDto() {
        LocalDateTime now = LocalDateTime.now();
        QueueEntryDto dto = QueueEntryDto.builder()
                .whatsappIdentifier("customer2@example.com")
                .customerName("Jane Smith")
                .status(QueueEntry.Status.ACTIVE)
                .joinedAt(now)
                .servedAt(null)
                .position(2)
                .metadata("{\"source\": \"whatsapp\"}")
                .business(BusinessMapper.INSTANCE.toDto(testBusiness))
                .waitTime(30L)
                .build();
        dto.setId(2L);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        return dto;
    }
}
