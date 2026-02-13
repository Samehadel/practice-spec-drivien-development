package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.application.dto.NotificationDto;
import com.example.whatsappqueue.domain.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface NotificationMapper extends BaseMapper<Notification, NotificationDto> {

    @Mapping(target = "business.queueEntries", ignore = true)
    @Override
    Notification toEntity(NotificationDto notificationDto);
    
}
