package com.example.whatsappqueue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

import com.example.whatsappqueue.domain.Notification;
import com.example.whatsappqueue.domain.Notification.MessageType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotificationDto extends BaseDto {
    private MessageType messageType;
    private String content;
    private Notification.Status status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String metadata;
    private BusinessDto business;
    private QueueEntryDto queueEntry;
}
