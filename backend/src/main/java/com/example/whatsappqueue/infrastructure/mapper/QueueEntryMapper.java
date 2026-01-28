package com.example.whatsappqueue.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface QueueEntryMapper extends BaseMapper {
    // QueueEntry entity and DTO mappings will be added here
    // Placeholder for User Story 1 implementation
}
