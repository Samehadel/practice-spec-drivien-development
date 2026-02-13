package com.example.whatsappqueue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public enum Status {
        ACTIVE,
        SERVED,
        NO_SHOW,
        LEFT
    }
}
