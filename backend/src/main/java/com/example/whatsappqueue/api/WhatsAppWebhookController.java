package com.example.whatsappqueue.api;

import com.example.whatsappqueue.application.QueueService;
import com.example.whatsappqueue.application.WhatsAppService;
import com.example.whatsappqueue.application.dto.BusinessDto;
import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.domain.Business;
import com.example.whatsappqueue.infrastructure.persistence.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/webhooks/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    private final WhatsAppService whatsAppService;
    private final QueueService queueService;
    private final BusinessRepository businessRepository;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String verifyToken) {
        
        log.info("Webhook verification request - mode: {}, verifyToken: {}", mode, verifyToken);
        
        // In a real implementation, verifyToken should be compared with a configured token
        // For now, we'll accept any verification request
        if ("subscribe".equals(mode)) {
            log.info("Webhook verification successful");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Webhook verification failed - invalid mode: {}", mode);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        try {
            log.debug("Received WhatsApp webhook payload: {}", payload);

            // Validate webhook payload
            if (!whatsAppService.isValidWebhookPayload(payload)) {
                log.warn("Invalid webhook payload received");
                return ResponseEntity.ok("Invalid payload");
            }

            // Extract message and sender information
            String message = whatsAppService.extractMessageFromWebhook(payload);
            String whatsappIdentifier = whatsAppService.extractWhatsAppIdentifier(payload);

            if (message == null || whatsappIdentifier == null) {
                log.warn("Could not extract message or sender from webhook payload");
                return ResponseEntity.ok("No message content");
            }

            log.info("Processing message from {}: {}", whatsappIdentifier, message);

            // Process the message based on content
            String response = processMessage(message, whatsappIdentifier);
            
            log.info("Message processed successfully for {}", whatsappIdentifier);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing WhatsApp webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook");
        }
    }

    private String processMessage(String message, String whatsappIdentifier) {
        String normalizedMessage = message.trim().toLowerCase();

        // Handle join queue commands
        if (isJoinCommand(normalizedMessage)) {
            return handleJoinQueue(whatsappIdentifier, message);
        }
        
        // Handle leave queue commands
        else if (isLeaveCommand(normalizedMessage)) {
            return handleLeaveQueue(whatsappIdentifier);
        }
        
        // Handle status check commands
        else if (isStatusCommand(normalizedMessage)) {
            return handleStatusCheck(whatsappIdentifier);
        }
        
        // Handle help command
        else if (isHelpCommand(normalizedMessage)) {
            return getHelpMessage();
        }
        
        // Unknown command
        else {
            return getUnknownCommandMessage();
        }
    }

    private String handleJoinQueue(String whatsappIdentifier, String originalMessage) {
        try {
            // Extract customer name from message if provided
            String customerName = extractCustomerName(originalMessage);
            
            // For User Story 1, we'll assume a default business ID (this should be configurable)
            // In a real implementation, this would be determined by the WhatsApp phone number
            Long businessId = 1L; // Default business for MVP
            
            QueueEntryDto queueEntry = queueService.joinQueue(whatsappIdentifier, customerName, businessId);
            
            // Send confirmation message
            whatsAppService.sendJoinConfirmation(queueEntry);
            
            return "Join queue processed successfully";
            
        } catch (Exception e) {
            log.error("Error joining queue: {}", e.getMessage(), e);
            return "Error joining queue";
        }
    }

    private String handleLeaveQueue(String whatsappIdentifier) {
        try {
            // For User Story 1, we'll assume a default business ID
            Long businessId = 1L;
            
            QueueEntryDto queueEntry = queueService.leaveQueue(whatsappIdentifier, businessId);
            
            // Send leave confirmation
            String businessName = queueEntry.getBusiness().getName();
            whatsAppService.sendLeaveConfirmation(whatsappIdentifier, businessName);
            
            return "Leave queue processed successfully";
            
        } catch (Exception e) {
            log.error("Error leaving queue: {}", e.getMessage(), e);
            return "Error leaving queue";
        }
    }

    private String handleStatusCheck(String whatsappIdentifier) {
        try {
            // For User Story 1, we'll assume a default business ID
            Long businessId = 1L;
            
            Optional<QueueEntryDto> queueEntry = queueService.getQueueEntryByWhatsApp(whatsappIdentifier, businessId);
            
            if (queueEntry.isPresent()) {
                QueueEntryDto entry = queueEntry.get();
                // Send position update
                whatsAppService.sendPositionUpdate(entry);
                return "Status check processed successfully";
            } else {
                // Customer is not in queue
                String businessName = businessRepository.findById(businessId)
                        .map(Business::getName)
                        .orElse("the business");
                
                String message = String.format(
                    "ℹ️ *Queue Status*\\n\\n" +
                    "You are not currently in the queue at %s.\\n\\n" +
                    "Reply 'JOIN' to join the queue! 🚀",
                    businessName
                );
                
                // Send status message
                whatsAppService.sendMessage(whatsappIdentifier, message, 
                        com.example.whatsappqueue.domain.Notification.MessageType.POSITION_UPDATE, null);
                
                return "Not in queue";
            }
            
        } catch (Exception e) {
            log.error("Error checking status: {}", e.getMessage(), e);
            return "Error checking status";
        }
    }

    private boolean isJoinCommand(String message) {
        return message.equals("join") || 
               message.equals("j") || 
               message.startsWith("join ") || 
               message.startsWith("j ") ||
               message.equals("hi") ||
               message.equals("hello");
    }

    private boolean isLeaveCommand(String message) {
        return message.equals("leave") || 
               message.equals("l") || 
               message.startsWith("leave ") || 
               message.startsWith("l ") ||
               message.equals("exit") ||
               message.equals("quit");
    }

    private boolean isStatusCommand(String message) {
        return message.equals("status") || 
               message.equals("s") || 
               message.startsWith("status ") || 
               message.startsWith("s ") ||
               message.equals("position") ||
               message.equals("pos");
    }

    private boolean isHelpCommand(String message) {
        return message.equals("help") || 
               message.equals("h") || 
               message.equals("?") ||
               message.equals("commands");
    }

    private String extractCustomerName(String message) {
        // Extract name from "join John Doe" or "j John Doe"
        String normalizedMessage = message.trim();
        
        if (normalizedMessage.toLowerCase().startsWith("join ")) {
            return normalizedMessage.substring(5).trim();
        } else if (normalizedMessage.toLowerCase().startsWith("j ")) {
            return normalizedMessage.substring(2).trim();
        }
        
        // Return null if no name provided, will use default in service
        return null;
    }

    private String getHelpMessage() {
        return "🤖 *Queue Bot Commands*\\n\\n" +
               "*Join Queue:*\\n" +
               "• `join` or `hi`\\n" +
               "• `join [Your Name]`\\n" +
               "• `j [Your Name]`\\n\\n" +
               "*Leave Queue:*\\n" +
               "• `leave` or `exit`\\n" +
               "• `l`\\n\\n" +
               "*Check Status:*\\n" +
               "• `status` or `position`\\n" +
               "• `s` or `pos`\\n\\n" +
               "*Help:*\\n" +
               "• `help` or `?`\\n\\n" +
               "Example: `join John Doe`";
    }

    private String getUnknownCommandMessage() {
        return "❓ *Unknown Command*\\n\\n" +
               "Reply `help` to see available commands.\\n" +
               "Or simply say `hi` to join the queue! 🚀";
    }
}
