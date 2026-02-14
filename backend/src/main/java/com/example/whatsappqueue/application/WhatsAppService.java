package com.example.whatsappqueue.application;

import com.example.whatsappqueue.application.dto.NotificationDto;
import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.common.exception.ExceptionService;
import com.example.whatsappqueue.domain.ApplicationError;
import com.example.whatsappqueue.domain.Notification;
import com.example.whatsappqueue.infrastructure.mapper.BusinessMapper;
import com.example.whatsappqueue.infrastructure.mapper.NotificationMapper;
import com.example.whatsappqueue.infrastructure.mapper.QueueEntryMapper;
import com.example.whatsappqueue.infrastructure.persistence.NotificationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final BusinessMapper businessMapper;
    private final QueueEntryMapper queueEntryMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappApiToken;

    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;

    @Transactional
    public NotificationDto sendJoinConfirmation(QueueEntryDto queueEntryDto) {
        String message = String.format(
            "✅ *You're in the queue!*\\n\\n" +
            "📍 Position: %d\\n" +
            "⏱️ Estimated wait: %d minutes\\n" +
            "🏪 %s\\n\\n" +
            "We'll notify you when it's almost your turn!",
            queueEntryDto.getPosition(),
            queueEntryDto.getWaitTime(),
            queueEntryDto.getBusiness().getName()
        );

        return sendMessage(queueEntryDto.getWhatsappIdentifier(), message, Notification.MessageType.JOIN_CONFIRMATION, queueEntryDto);
    }

    @Transactional
    public NotificationDto sendPositionUpdate(QueueEntryDto queueEntryDto) {
        String message = String.format(
            "📊 *Queue Update*\\n\\n" +
            "📍 New position: %d\\n" +
            "⏱️ Estimated wait: %d minutes\\n\\n" +
            "You're getting closer! 🎯",
            queueEntryDto.getPosition(),
            queueEntryDto.getWaitTime()
        );

        return sendMessage(queueEntryDto.getWhatsappIdentifier(), message, Notification.MessageType.POSITION_UPDATE, queueEntryDto);
    }

    @Transactional
    public NotificationDto sendNearlyYourTurn(QueueEntryDto queueEntryDto) {
        String message = String.format(
            "🔔 *Nearly Your Turn!*\\n\\n" +
            "📍 Position: %d\\n" +
            "⏱️ Estimated wait: %d minutes\\n\\n" +
            "Get ready! You're almost up! 🚀",
            queueEntryDto.getPosition(),
            queueEntryDto.getWaitTime()
        );

        return sendMessage(queueEntryDto.getWhatsappIdentifier(), message, Notification.MessageType.NEARLY_YOUR_TURN, queueEntryDto);
    }

    @Transactional
    public NotificationDto sendYouAreNext(QueueEntryDto queueEntryDto) {
        String message = String.format(
            "🎉 *You're NEXT!*\\n\\n" +
            "Please make your way to the service counter.\\n\\n" +
            "📍 Position: %d\\n" +
            "🏪 %s\\n\\n" +
            "Good luck! ✨",
            queueEntryDto.getPosition(),
            queueEntryDto.getBusiness().getName()
        );

        return sendMessage(queueEntryDto.getWhatsappIdentifier(), message, Notification.MessageType.YOU_ARE_NEXT, queueEntryDto);
    }

    @Transactional
    public NotificationDto sendLeaveConfirmation(String whatsappIdentifier, String businessName) {
        String message = String.format(
            "✅ *Left Queue Successfully*\\n\\n" +
            "You have been removed from the queue at %s.\\n\\n" +
            "Thank you for using our service! 👋",
            businessName
        );

        return sendMessage(whatsappIdentifier, message, Notification.MessageType.LEAVE_CONFIRMATION, null);
    }

    @Transactional
    public NotificationDto sendQueueClosed(String whatsappIdentifier, String businessName) {
        String message = String.format(
            "🔒 *Queue Closed*\\n\\n" +
            "The queue at %s is currently closed.\\n\\n" +
            "Please try again later during business hours.\\n\\n" +
            "Thank you for your understanding! 🙏",
            businessName
        );

        return sendMessage(whatsappIdentifier, message, Notification.MessageType.QUEUE_CLOSED, null);
    }

    public NotificationDto sendMessage(String whatsappIdentifier, String content, Notification.MessageType messageType, QueueEntryDto queueEntryDto) {
        try {
            log.info("Sending WhatsApp message to {}: {}", whatsappIdentifier, messageType);

            // Create notification record
            Notification notification = Notification.builder()
                    .messageType(messageType)
                    .content(content)
                    .status(Notification.Status.PENDING)
                    .sentAt(LocalDateTime.now())
                    .business(queueEntryDto != null ? 
                            businessMapper.toEntity(queueEntryDto.getBusiness()) : null)
                    .queueEntry(queueEntryDto != null ? 
                            queueEntryMapper.toEntity(queueEntryDto) : null)
                    .metadata(Map.of("messageId", UUID.randomUUID().toString()).toString())
                    .build();

            // Send WhatsApp message
            boolean messageSent = sendWhatsAppMessage(whatsappIdentifier, content);

            if (messageSent) {
                notification.setStatus(Notification.Status.SENT);
                notification.setDeliveredAt(LocalDateTime.now());
                log.info("WhatsApp message sent successfully to {}", whatsappIdentifier);
            } else {
                notification.setStatus(Notification.Status.FAILED);
                log.error("Failed to send WhatsApp message to {}", whatsappIdentifier);
            }

            Notification savedNotification = notificationRepository.save(notification);
            return notificationMapper.toDto(savedNotification);

        } catch (Exception e) {
            log.error("Error sending WhatsApp message to {}: {}", whatsappIdentifier, e.getMessage(), e);
            throw ExceptionService.InternalExceptionBuilder.internalException(ApplicationError.INTERNAL_ERROR, e);
        }
    }

    private boolean sendWhatsAppMessage(String to, String message) {
        try {
            String url = String.format("%s/%s/messages", whatsappApiUrl, phoneNumberId);

            Map<String, Object> requestBody = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of(
                    "body", message
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(whatsappApiToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("WhatsApp API response: {}", response.getBody());
                return true;
            } else {
                log.error("WhatsApp API error response: {}", response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            log.error("Error calling WhatsApp API: {}", e.getMessage(), e);
            return false;
        }
    }

    public String extractMessageFromWebhook(String webhookPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(webhookPayload);
            
            // Navigate to the message content
            JsonNode entry = rootNode.path("entry");
            if (entry.isArray() && entry.size() > 0) {
                JsonNode changes = entry.get(0).path("changes");
                if (changes.isArray() && changes.size() > 0) {
                    JsonNode value = changes.get(0).path("value");
                    JsonNode messages = value.path("messages");
                    if (messages.isArray() && messages.size() > 0) {
                        JsonNode message = messages.get(0);
                        if (message.path("type").asText().equals("text")) {
                            return message.path("text").path("body").asText();
                        }
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting message from webhook payload: {}", e.getMessage(), e);
            return null;
        }
    }

    public String extractWhatsAppIdentifier(String webhookPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(webhookPayload);
            
            // Navigate to the phone number
            JsonNode entry = rootNode.path("entry");
            if (entry.isArray() && entry.size() > 0) {
                JsonNode changes = entry.get(0).path("changes");
                if (changes.isArray() && changes.size() > 0) {
                    JsonNode value = changes.get(0).path("value");
                    JsonNode contacts = value.path("contacts");
                    if (contacts.isArray() && contacts.size() > 0) {
                        JsonNode contact = contacts.get(0);
                        return contact.path("wa_id").asText();
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting WhatsApp identifier from webhook payload: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean isValidWebhookPayload(String webhookPayload) {
        try {
            JsonNode rootNode = objectMapper.readTree(webhookPayload);
            
            // Check if it's a message webhook
            JsonNode entry = rootNode.path("entry");
            if (entry.isArray() && entry.size() > 0) {
                JsonNode changes = entry.get(0).path("changes");
                if (changes.isArray() && changes.size() > 0) {
                    JsonNode value = changes.get(0).path("value");
                    JsonNode messages = value.path("messages");
                    return messages.isArray() && messages.size() > 0;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error validating webhook payload: {}", e.getMessage(), e);
            return false;
        }
    }
}
