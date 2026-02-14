package com.example.whatsappqueue.infrastructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.domain.QueueEntry;

@Component
public class QueueEntryMapper implements BaseMapper<QueueEntry, QueueEntryDto> {

    @Override
    public QueueEntryDto toDto(QueueEntry entity) {
        if (entity == null) {
            return null;
        }
        
        QueueEntryDto dto = QueueEntryDto.builder()
                .whatsappIdentifier(entity.getWhatsappIdentifier())
                .customerName(entity.getCustomerName())
                .status(entity.getStatus())
                .joinedAt(entity.getJoinedAt())
                .servedAt(entity.getServedAt())
                .position(entity.getPosition())
                .metadata(entity.getMetadata())
                .business(entity.getBusiness() != null ? 
                    BusinessMapper.INSTANCE.toDto(entity.getBusiness()) : null)
                .waitTime(entity.waitTime())
                .build();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    @Override
    public QueueEntry toEntity(QueueEntryDto dto) {
        if (dto == null) {
            return null;
        }
        
        QueueEntry entity = QueueEntry.builder()
                .whatsappIdentifier(dto.getWhatsappIdentifier())
                .customerName(dto.getCustomerName())
                .status(dto.getStatus())
                .joinedAt(dto.getJoinedAt())
                .servedAt(dto.getServedAt())
                .position(dto.getPosition())
                .metadata(dto.getMetadata())
                .build();
        entity.setId(dto.getId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    @Override
    public List<QueueEntryDto> toDtoList(List<QueueEntry> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueEntry> toEntityList(List<QueueEntryDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
