package com.example.whatsappqueue.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.whatsappqueue.application.QueueService;
import com.example.whatsappqueue.application.QueueValidationService;
import com.example.whatsappqueue.application.WhatsAppService;
import com.example.whatsappqueue.application.dto.BusinessDto;
import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.common.exception.ResourceAlreadyExistsException;
import com.example.whatsappqueue.common.exception.ResourceNotFoundException;
import com.example.whatsappqueue.common.exception.ValidationException;
import com.example.whatsappqueue.domain.QueueEntry;
import com.example.whatsappqueue.infrastructure.cache.QueueCacheService;
import com.example.whatsappqueue.infrastructure.persistence.BusinessRepository;
import com.example.whatsappqueue.infrastructure.persistence.QueueEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@DisplayName("WhatsApp Webhook Controller Integration Tests")
class WhatsAppWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private QueueService queueService;
    
    @Mock
    private WhatsAppService whatsappService;
    
    @Mock
    private BusinessRepository businessRepository;
    
    @Mock
    private QueueValidationService validationService;
    
    @Mock
    private QueueCacheService cacheService;
    
    @Mock
    private QueueEntryRepository queueEntryRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private BusinessDto testBusiness;
    
    @BeforeEach
    void setUp() {
        testBusiness = BusinessDto.builder()
                .name("Test Restaurant")
                .whatsappPhoneNumber("+1234567890")
                .queueOpen(true)
                .averageServiceTimeMinutes(15)
                .notificationThreshold(3)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(new WhatsAppWebhookController(whatsappService, queueService, businessRepository))
                .build();
    }
    
    @Test
    @DisplayName("Should process join message when queue is open")
    void shouldProcessJoinMessageWhenQueueIsOpen() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+1234567890\",\"id\":\"msg123\",\"text\":{\"body\":\"Hi\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any()))
        .thenReturn(
            QueueEntryDto.builder()
                    .business(testBusiness)
                    .whatsappIdentifier("+1234567890")
                    .status(QueueEntry.Status.ACTIVE)
                    .position(1)
                    .joinedAt(any())
                    .build()
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("processed"))
                .andExpect(jsonPath("$.message").value("Customer joined successfully"));
    }
    
    @Test
    @DisplayName("Should reject join message when queue is closed")
    void shouldRejectJoinMessageWhenQueueIsClosed() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+1234567890\",\"id\":\"msg123\",\"text\":{\"body\":\"Hi\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any())).thenThrow(
            new ValidationException("Queue is currently closed for business: " + testBusiness.getName())
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ignored"))
                .andExpect(jsonPath("$.message").value("Queue is currently closed"));
    }
    
    @Test
    @DisplayName("Should handle duplicate join request")
    void shouldHandleDuplicateJoinRequest() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+1234567890\",\"id\":\"msg124\",\"text\":{\"body\":\"Hi\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any())).thenThrow(
            new ResourceAlreadyExistsException("Customer already in queue with +1234567890")
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("processed"))
                .andExpect(jsonPath("$.message").value("Customer is already in queue"));
    }
    
    @Test
    @DisplayName("Should handle invalid message format")
    void shouldHandleInvalidMessageFormat() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"invalid\",\"id\":\"msg125\",\"text\":{\"body\":\"Invalid\"}}]}";
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ignored"))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @DisplayName("Should handle business not found error")
    void shouldHandleBusinessNotFoundError() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+9999999999\",\"id\":\"msg126\",\"text\":{\"body\":\"Hi\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any()))
        .thenThrow(
            new ResourceNotFoundException("Business not found")
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ignored"))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @DisplayName("Should handle validation exception")
    void shouldHandleValidationException() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+1234567890\",\"id\":\"msg127\",\"text\":{\"body\":\"\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any())).thenThrow(
            new ValidationException("Invalid message format")
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ignored"))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @DisplayName("Should handle internal server error")
    void shouldHandleInternalServerError() throws Exception {
        // Given
        String whatsappMessage = "{\"object\":\"user\",\"entry\":[{\"changes\":[{\"field\":\"messaging\",\"value\":{\"messaging_product\":\"whatsapp\"}}],\"messages\":[{\"from\":\"+1234567890\",\"id\":\"msg128\",\"text\":{\"body\":\"Hi\"}}]}";
        
        when(queueService.joinQueue(any(), any(), any())).thenThrow(
            new RuntimeException("Database connection failed")
        );
        
        // When
        mockMvc.perform(post("/webhooks/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(whatsappMessage)
                .header("X-Hub-Signature", "test-signature")
        )
        
        // Then
        .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("ignored"))
                .andExpect(jsonPath("$.message").exists());
    }
}
