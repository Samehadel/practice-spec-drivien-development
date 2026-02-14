package com.example.whatsappqueue.api;

import com.example.whatsappqueue.domain.QueueEntry;
import com.example.whatsappqueue.domain.Business;
import com.example.whatsappqueue.application.QueueService;
import com.example.whatsappqueue.application.QueueValidationService;
import com.example.whatsappqueue.infrastructure.cache.QueueCacheService;
import com.example.whatsappqueue.infrastructure.persistence.QueueEntryRepository;
import com.example.whatsappqueue.common.exception.ValidationException;
import com.example.whatsappqueue.common.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Queue Controller Contract Tests")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private QueueService queueService;
    
    @Mock
    private QueueValidationService validationService;
    
    @Mock
    private QueueCacheService cacheService;
    
    @Mock
    private QueueEntryRepository queueEntryRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private Business testBusiness;
    
    @BeforeEach
    void setUp() {
        testBusiness = Business.builder()
                .id(UUID.randomUUID())
                .name("Test Restaurant")
                .whatsappPhoneNumber("+1234567890")
                .queueOpen(true)
                .averageServiceTimeMinutes(15)
                .notificationThreshold(3)
                .build();
        
        mockMvc = MockMvcBuilders.standaloneSetup(new QueueController())
                .build();
    }
    
    @Test
    @DisplayName("Should return queue status with active entries")
    void shouldReturnQueueStatusWithActiveEntries() throws Exception {
        // Given
        String businessId = testBusiness.getId();
        java.util.List<QueueEntry> activeEntries = java.util.List.of(
            QueueEntry.builder()
                    .id(UUID.randomUUID())
                    .businessId(businessId)
                    .whatsappIdentifier("+1234567890")
                    .status(QueueEntry.Status.ACTIVE)
                    .position(1)
                    .joinedAt(java.time.LocalDateTime.now())
                    .build(),
            QueueEntry.builder()
                    .id(UUID.randomUUID())
                    .businessId(businessId)
                    .whatsappIdentifier("+1234567891")
                    .status(QueueEntry.Status.ACTIVE)
                    .position(2)
                    .joinedAt(java.time.LocalDateTime.now())
                    .build()
        );
        
        when(queueService.getBusinessQueue(businessId)).thenReturn(
            Map.of(
                "business", testBusiness,
                "isOpen", true,
                "activeEntries", activeEntries,
                "estimatedWaitTime", 15
            )
        );
        
        // When
        mockMvc.perform(get("/businesses/{businessId}/queue"))
                .contentType(MediaType.APPLICATION_JSON)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.business").exists())
                .andExpect(jsonPath("$.isOpen").value(true))
                .andExpect(jsonPath("$.activeEntries").isArray())
                .andExpect(jsonPath("$.activeEntries[0].whatsappIdentifier").value("+1234567890"))
                .andExpect(jsonPath("$.activeEntries[0].position").value(1))
                .andExpect(jsonPath("$.activeEntries[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.activeEntries[1].whatsappIdentifier").value("+1234567891"))
                .andExpect(jsonPath("$.activeEntries[1].position").value(2))
                .andExpect(jsonPath("$.activeEntries[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$.estimatedWaitTime").value(15));
    }
    
    @Test
    @DisplayName("Should return 404 when business not found")
    void shouldReturn404WhenBusinessNotFound() throws Exception {
        // Given
        String businessId = UUID.randomUUID().toString();
        
        when(queueService.getBusinessQueue(businessId)).thenThrow(
            new ResourceNotFoundException("Business", businessId)
        );
        
        // When
        mockMvc.perform(get("/businesses/{businessId}/queue"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("RES_001"))
                .andExpect(jsonPath("$.error.message").value("Business not found with " + businessId));
    }
    
    @Test
    @DisplayName("Should handle advance queue request successfully")
    void shouldAdvanceQueueSuccessfully() throws Exception {
        // Given
        String businessId = testBusiness.getId();
        UUID queueEntryId = UUID.randomUUID();
        String action = "serve";
        String notes = "Customer served successfully";
        
        when(queueService.advanceQueue(businessId, queueEntryId, action, notes)).thenReturn(
            QueueEntry.builder()
                    .id(queueEntryId)
                    .businessId(businessId)
                    .status(QueueEntry.Status.SERVED)
                    .servedAt(any())
                    .build()
        );
        
        // When
        mockMvc.perform(post("/businesses/{businessId}/queue/advance")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"" + action + "\",\"notes\":\"" + notes + "\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servedEntry.id").value(queueEntryId.toString()))
                .andExpect(jsonPath("$.servedEntry.status").value("SERVED"))
                .andExpect(jsonPath("$.nextEntry").exists())
                .andExpect(jsonPath("$.nextEntry.whatsappIdentifier").value("+1234567890"));
                .andExpect(jsonPath("$.nextEntry.position").value(1));
    }
    
    @Test
    @DisplayName("Should handle advance queue when queue is empty")
    void shouldHandleAdvanceQueueWhenEmpty() throws Exception {
        // Given
        String businessId = testBusiness.getId();
        String action = "serve";
        String notes = "Customer served successfully";
        
        when(queueService.advanceQueue(businessId, null, action, notes)).thenThrow(
            new ValidationException("No current entry to advance")
        );
        
        // When
        mockMvc.perform(post("/businesses/{businessId}/queue/advance")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"" + action + "\",\"notes\":\"" + notes + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("VAL_001"))
                .andExpect(jsonPath("$.error.message").value("No current entry to advance"));
    }
}
