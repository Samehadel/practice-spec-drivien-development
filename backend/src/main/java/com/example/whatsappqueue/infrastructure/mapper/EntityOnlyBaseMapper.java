package com.example.whatsappqueue.infrastructure.mapper;

import java.util.List;

import com.example.whatsappqueue.application.dto.BaseDto;
import com.example.whatsappqueue.domain.BaseEntity;

public interface EntityOnlyBaseMapper <E extends BaseEntity, D extends BaseDto> {
    D toDto(E entity);
    List<D> toDtoList(List<E> entities);
}
