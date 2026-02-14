package com.example.whatsappqueue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
