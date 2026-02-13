package com.example.whatsappqueue.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessDto extends BaseDto {
    private String name;
    private String serviceType;
    private String whatsappPhoneNumber;
    private Boolean queueOpen;
    private Integer averageServiceTimeMinutes;
    private Integer notificationThreshold;
}
