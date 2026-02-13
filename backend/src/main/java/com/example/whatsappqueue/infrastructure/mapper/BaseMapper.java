package com.example.whatsappqueue.infrastructure.mapper;

import com.example.whatsappqueue.domain.BaseEntity;
import com.example.whatsappqueue.application.dto.BaseDto;

import java.util.List;

interface BaseMapper<E extends BaseEntity, D extends BaseDto> {
    
    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDtoList(List<E> entities);
    List<E> toEntityList(List<D> dtos);
}
