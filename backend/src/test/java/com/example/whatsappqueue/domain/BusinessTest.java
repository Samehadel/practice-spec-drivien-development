package com.example.whatsappqueue.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

class BusinessTest {

    @Test
    @DisplayName("Should create Business with all required fields")
    void shouldCreateBusinessWithAllRequiredFields() {
        // Given
        String name = "Test Business";
        String serviceType = "Restaurant";
        String whatsappPhoneNumber = "+1234567890";
        
        // When
        Business business = Business.builder()
                .name(name)
                .serviceType(serviceType)
                .whatsappPhoneNumber(whatsappPhoneNumber)
                .queueOpen(true)
                .averageServiceTimeMinutes(10)
                .notificationThreshold(3)
                .build();
        
        // Then
        assertThat(business).isNotNull();
        assertThat(business.getName()).isEqualTo(name);
        assertThat(business.getServiceType()).isEqualTo(serviceType);
        assertThat(business.getWhatsappPhoneNumber()).isEqualTo(whatsappPhoneNumber);
        assertThat(business.getQueueOpen()).isTrue();
        assertThat(business.getAverageServiceTimeMinutes()).isEqualTo(10);
        assertThat(business.getNotificationThreshold()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should create Business with default values")
    void shouldCreateBusinessWithDefaultValues() {
        // When
        Business business = Business.builder().build();
        
        // Then
        assertThat(business.getQueueOpen()).isTrue();
        assertThat(business.getAverageServiceTimeMinutes()).isEqualTo(10);
        assertThat(business.getNotificationThreshold()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should update Business fields")
    void shouldUpdateBusinessFields() {
        // Given
        Business business = Business.builder()
                .name("Original Name")
                .serviceType("Original Service")
                .build();
        
        // When
        business.setName("Updated Name");
        business.setServiceType("Updated Service");
        business.setQueueOpen(false);
        
        // Then
        assertThat(business.getName()).isEqualTo("Updated Name");
        assertThat(business.getServiceType()).isEqualTo("Updated Service");
        assertThat(business.getQueueOpen()).isFalse();
    }
}
