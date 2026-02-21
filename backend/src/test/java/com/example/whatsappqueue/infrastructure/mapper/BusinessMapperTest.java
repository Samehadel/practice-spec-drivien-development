package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.application.dto.BusinessDto;
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
@DisplayName("BusinessMapper Unit Tests")
class BusinessMapperTest {

    private BusinessMapper businessMapper;
    private Business testBusiness;
    private BusinessDto testBusinessDto;

    @BeforeEach
    void setUp() {
        businessMapper = BusinessMapper.INSTANCE;
        
        LocalDateTime now = LocalDateTime.now();
        Long businessId = 1L;
        
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
        
        testBusinessDto = BusinessDto.builder()
                .name("Test Restaurant")
                .serviceType("Restaurant")
                .whatsappPhoneNumber("+1234567890")
                .queueOpen(true)
                .averageServiceTimeMinutes(15)
                .notificationThreshold(3)
                .build();
        testBusinessDto.setId(businessId);
        testBusinessDto.setCreatedAt(now);
        testBusinessDto.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should map Business entity to BusinessDto")
    void shouldMapEntityToDto() {
        // When
        BusinessDto result = businessMapper.toDto(testBusiness);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBusiness.getId());
        assertThat(result.getName()).isEqualTo(testBusiness.getName());
        assertThat(result.getServiceType()).isEqualTo(testBusiness.getServiceType());
        assertThat(result.getWhatsappPhoneNumber()).isEqualTo(testBusiness.getWhatsappPhoneNumber());
        assertThat(result.getQueueOpen()).isEqualTo(testBusiness.getQueueOpen());
        assertThat(result.getAverageServiceTimeMinutes()).isEqualTo(testBusiness.getAverageServiceTimeMinutes());
        assertThat(result.getNotificationThreshold()).isEqualTo(testBusiness.getNotificationThreshold());
        assertThat(result.getCreatedAt()).isEqualTo(testBusiness.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testBusiness.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map BusinessDto to Business entity")
    void shouldMapDtoToEntity() {
        // When
        Business result = businessMapper.toEntity(testBusinessDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBusinessDto.getId());
        assertThat(result.getName()).isEqualTo(testBusinessDto.getName());
        assertThat(result.getServiceType()).isEqualTo(testBusinessDto.getServiceType());
        assertThat(result.getWhatsappPhoneNumber()).isEqualTo(testBusinessDto.getWhatsappPhoneNumber());
        assertThat(result.getQueueOpen()).isEqualTo(testBusinessDto.getQueueOpen());
        assertThat(result.getAverageServiceTimeMinutes()).isEqualTo(testBusinessDto.getAverageServiceTimeMinutes());
        assertThat(result.getNotificationThreshold()).isEqualTo(testBusinessDto.getNotificationThreshold());
        assertThat(result.getCreatedAt()).isEqualTo(testBusinessDto.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testBusinessDto.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when mapping null entity to DTO")
    void shouldReturnNullWhenMappingNullEntityToDto() {
        // When
        BusinessDto result = businessMapper.toDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when mapping null DTO to entity")
    void shouldReturnNullWhenMappingNullDtoToEntity() {
        // When
        Business result = businessMapper.toEntity(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map list of Business entities to list of BusinessDtos")
    void shouldMapEntityListToDtoList() {
        // Given
        List<Business> entities = List.of(testBusiness, createAnotherBusiness());

        // When
        List<BusinessDto> result = businessMapper.toDtoList(entities);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(testBusiness.getName());
        assertThat(result.get(1).getName()).isEqualTo("Another Restaurant");
    }

    @Test
    @DisplayName("Should map list of BusinessDtos to list of Business entities")
    void shouldMapDtoListToEntityList() {
        // Given
        List<BusinessDto> dtos = List.of(testBusinessDto, createAnotherBusinessDto());

        // When
        List<Business> result = businessMapper.toEntityList(dtos);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo(testBusinessDto.getName());
        assertThat(result.get(1).getName()).isEqualTo("Another Restaurant");
    }

    @Test
    @DisplayName("Should handle empty entity list")
    void shouldHandleEmptyEntityList() {
        // Given
        List<Business> entities = List.of();

        // When
        List<BusinessDto> result = businessMapper.toDtoList(entities);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty DTO list")
    void shouldHandleEmptyDtoList() {
        // Given
        List<BusinessDto> dtos = List.of();

        // When
        List<Business> result = businessMapper.toEntityList(dtos);

        // Then
        assertThat(result).isEmpty();
    }

    private Business createAnotherBusiness() {
        LocalDateTime now = LocalDateTime.now();
        Business business = Business.builder()
                .name("Another Restaurant")
                .serviceType("Clinic")
                .whatsappPhoneNumber("+0987654321")
                .queueOpen(false)
                .averageServiceTimeMinutes(20)
                .notificationThreshold(5)
                .build();
        business.setId(2L);
        business.setCreatedAt(now);
        business.setUpdatedAt(now);
        return business;
    }

    private BusinessDto createAnotherBusinessDto() {
        LocalDateTime now = LocalDateTime.now();
        BusinessDto dto = BusinessDto.builder()
                .name("Another Restaurant")
                .serviceType("Clinic")
                .whatsappPhoneNumber("+0987654321")
                .queueOpen(false)
                .averageServiceTimeMinutes(20)
                .notificationThreshold(5)
                .build();
        dto.setId(2L);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        return dto;
    }
}
