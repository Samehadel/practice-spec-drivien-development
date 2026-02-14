package com.example.whatsappqueue.infrastructure.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.whatsappqueue.application.dto.NotificationDto;
import com.example.whatsappqueue.domain.Notification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMapper implements EntityOnlyBaseMapper<Notification, NotificationDto> {

    private final BusinessMapper businessMapper;
    private final QueueEntryMapper queueEntryMapper;

    @Override
    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }
        
        NotificationDto dto = NotificationDto.builder()
                .messageType(entity.getMessageType())
                .content(entity.getContent())
                .status(entity.getStatus())
                .sentAt(entity.getSentAt())
                .deliveredAt(entity.getDeliveredAt())
                .metadata(entity.getMetadata())
                .business(entity.getBusiness() != null ? 
                    businessMapper.toDto(entity.getBusiness()) : null)
                .queueEntry(entity.getQueueEntry() != null ? 
                    queueEntryMapper.toDto(entity.getQueueEntry()) : null)
                .build();

        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    public List<NotificationDto> toDtoList(List<Notification> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
