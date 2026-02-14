package com.example.whatsappqueue.infrastructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.whatsappqueue.application.dto.BusinessDto;
import com.example.whatsappqueue.domain.Business;

@Component
public class BusinessMapper implements BaseMapper<Business, BusinessDto> {

    public static final BusinessMapper INSTANCE = new BusinessMapper();

    @Override
    public BusinessDto toDto(Business entity) {
        if (entity == null) {
            return null;
        }
        
    
        BusinessDto dto = BusinessDto.builder()
                .name(entity.getName())
                .serviceType(entity.getServiceType())
                .whatsappPhoneNumber(entity.getWhatsappPhoneNumber())
                .queueOpen(entity.getQueueOpen())
                .averageServiceTimeMinutes(entity.getAverageServiceTimeMinutes())
                .notificationThreshold(entity.getNotificationThreshold())
                .build();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    @Override
    public Business toEntity(BusinessDto dto) {
        if (dto == null) {
            return null;
        }
        
        Business entity = Business.builder()
                .name(dto.getName())
                .serviceType(dto.getServiceType())
                .whatsappPhoneNumber(dto.getWhatsappPhoneNumber())
                .queueOpen(dto.getQueueOpen())
                .averageServiceTimeMinutes(dto.getAverageServiceTimeMinutes())
                .notificationThreshold(dto.getNotificationThreshold())
                .build();
        entity.setId(dto.getId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    @Override
    public List<BusinessDto> toDtoList(List<Business> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Business> toEntityList(List<BusinessDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
