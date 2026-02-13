package com.example.whatsappqueue.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.example.whatsappqueue.application.dto.BusinessDto;
import com.example.whatsappqueue.domain.Business;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BusinessMapper extends BaseMapper<Business, BusinessDto> {
}
