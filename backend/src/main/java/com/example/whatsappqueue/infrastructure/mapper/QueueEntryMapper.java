package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.domain.QueueEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface QueueEntryMapper extends BaseMapper<QueueEntry, QueueEntryDto> {
    
    @Mapping(target = "waitTime", expression = "java(queueEntry.calculateWaitTime())")
    @Override
    QueueEntryDto toDto(QueueEntry queueEntry);
    
    @Mapping(target = "waitTime", ignore = true)
    @Override
    QueueEntry toEntity(QueueEntryDto queueEntryDto);
}
