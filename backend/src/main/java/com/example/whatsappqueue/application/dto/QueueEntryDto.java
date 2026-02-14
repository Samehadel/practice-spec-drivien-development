package com.example.whatsappqueue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

import com.example.whatsappqueue.domain.QueueEntry.Status;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QueueEntryDto extends BaseDto {
    private String whatsappIdentifier;
    private String customerName;
    private Status status;
    private LocalDateTime joinedAt;
    private LocalDateTime servedAt;
    private Integer position;
    private String metadata;
    private BusinessDto business;
    private Long waitTime;
}
