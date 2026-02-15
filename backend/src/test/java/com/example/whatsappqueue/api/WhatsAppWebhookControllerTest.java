package com.example.whatsappqueue.api;

import com.example.whatsappqueue.application.WhatsAppService;
import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.domain.QueueEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("WhatsApp Webhook Integration Tests")
class WhatsAppWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WhatsAppService whatsAppService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new WhatsAppWebhookController(whatsAppService))
                .build();
    }

    @Test
    @DisplayName("Should handle join queue webhook successfully")
    void shouldHandleJoinQueueWebhookSuccessfully() throws Exception {
        // Given
        String webhookPayload = """
            {
                "object": "whatsapp_business_account",
                "entry": [{
                    "id": "123456789",
                    "changes": [{
                        "value": {
                            "messaging_product": "whatsapp",
                            "contacts": [{
                                "wa_id": "+1234567890",
                                "profile": {
                                    "name": "John Doe"
                                }
                            }],
                            "messages": [{
                                "from": "+1234567890",
                                "id": "msg123",
                                "timestamp": "1707995600",
                                "text": {
                                    "body": "join queue"
                                }
                            }]
                        }
                    }]
                }]
            }
            """;

        QueueEntryDto expectedQueueEntry = QueueEntryDto.builder()
                .whatsappIdentifier("+1234567890")
                .customerName("John Doe")
                .status(QueueEntry.Status.ACTIVE)
                .position(1)
                .build();

        when(whatsAppService.processMessage(eq("+1234567890"), eq("join queue"), any()))
                .thenReturn(expectedQueueEntry);

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(webhookPayload))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> senderCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> metadataCaptor = ArgumentCaptor.forClass(Map.class);

        verify(whatsAppService).processMessage(
                senderCaptor.capture(),
                messageCaptor.capture(),
                metadataCaptor.capture());

        assertThat(senderCaptor.getValue()).isEqualTo("+1234567890");
        assertThat(messageCaptor.getValue()).isEqualTo("join queue");
        assertThat(metadataCaptor.getValue()).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle leave queue webhook successfully")
    void shouldHandleLeaveQueueWebhookSuccessfully() throws Exception {
        // Given
        String webhookPayload = """
            {
                "object": "whatsapp_business_account",
                "entry": [{
                    "id": "123456789",
                    "changes": [{
                        "value": {
                            "messaging_product": "whatsapp",
                            "contacts": [{
                                "wa_id": "+1234567890",
                                "profile": {
                                    "name": "John Doe"
                                }
                            }],
                            "messages": [{
                                "from": "+1234567890",
                                "id": "msg124",
                                "timestamp": "1707995600",
                                "text": {
                                    "body": "leave queue"
                                }
                            }]
                        }
                    }]
                }]
            }
            """;

        when(whatsAppService.processMessage(eq("+1234567890"), eq("leave queue"), any()))
                .thenReturn(null);

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(webhookPayload))
                .andExpect(status().isOk());

        // Then
        verify(whatsAppService).processMessage(
                eq("+1234567890"),
                eq("leave queue"),
                any());
    }

    @Test
    @DisplayName("Should handle status check webhook successfully")
    void shouldHandleStatusCheckWebhookSuccessfully() throws Exception {
        // Given
        String webhookPayload = """
            {
                "object": "whatsapp_business_account",
                "entry": [{
                    "id": "123456789",
                    "changes": [{
                        "value": {
                            "messaging_product": "whatsapp",
                            "contacts": [{
                                "wa_id": "+1234567890",
                                "profile": {
                                    "name": "John Doe"
                                }
                            }],
                            "messages": [{
                                "from": "+1234567890",
                                "id": "msg125",
                                "timestamp": "1707995600",
                                "text": {
                                    "body": "status"
                                }
                            }]
                        }
                    }]
                }]
            }
            """;

        QueueEntryDto expectedQueueEntry = QueueEntryDto.builder()
                .whatsappIdentifier("+1234567890")
                .customerName("John Doe")
                .status(QueueEntry.Status.ACTIVE)
                .position(3)
                .waitTime(15L)
                .build();

        when(whatsAppService.processMessage(eq("+1234567890"), eq("status"), any()))
                .thenReturn(expectedQueueEntry);

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(webhookPayload))
                .andExpect(status().isOk());

        // Then
        verify(whatsAppService).processMessage(
                eq("+1234567890"),
                eq("status"),
                any());
    }

    @Test
    @DisplayName("Should handle invalid webhook payload gracefully")
    void shouldHandleInvalidWebhookPayloadGracefully() throws Exception {
        // Given
        String invalidPayload = """
            {
                "object": "invalid_object",
                "entry": []
            }
            """;

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isOk());

        // Then
        verify(whatsAppService, never()).processMessage(any(), any(), any());
    }

    @Test
    @DisplayName("Should handle webhook verification challenge")
    void shouldHandleWebhookVerificationChallenge() throws Exception {
        // Given
        String challengePayload = """
            {
                "hub.mode": "subscribe",
                "hub.verify_token": "test_token",
                "hub.challenge": "challenge123"
            }
            """;

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(challengePayload))
                .andExpect(status().isOk())
                .andExpect(content().string("challenge123"));
    }

    @Test
    @DisplayName("Should handle webhook with no messages")
    void shouldHandleWebhookWithNoMessages() throws Exception {
        // Given
        String noMessagesPayload = """
            {
                "object": "whatsapp_business_account",
                "entry": [{
                    "id": "123456789",
                    "changes": [{
                        "value": {
                            "messaging_product": "whatsapp",
                            "contacts": [],
                            "messages": []
                        }
                    }]
                }]
            }
            """;

        // When
        mockMvc.perform(post("/webhook/whatsapp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noMessagesPayload))
                .andExpect(status().isOk());

        // Then
        verify(whatsAppService, never()).processMessage(any(), any(), any());
    }
}
