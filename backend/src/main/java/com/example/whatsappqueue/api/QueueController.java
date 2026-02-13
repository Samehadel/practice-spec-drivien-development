package com.example.whatsappqueue.api;

import com.example.whatsappqueue.application.QueueService;
import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.common.exception.QueueEntryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/queue")
@RequiredArgsConstructor
@Slf4j
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/{businessId}")
    public ResponseEntity<List<QueueEntryDto>> getBusinessQueue(@PathVariable Long businessId) {
        try {
            log.info("Getting active queue entries for business {}", businessId);
            
            List<QueueEntryDto> activeEntries = queueService.getActiveQueueEntries(businessId);
            
            return ResponseEntity.ok(activeEntries);
            
        } catch (Exception e) {
            log.error("Error getting queue for business {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{businessId}/length")
    public ResponseEntity<Integer> getQueueLength(@PathVariable Long businessId) {
        try {
            log.info("Getting queue length for business {}", businessId);
            
            Integer queueLength = queueService.getQueueLength(businessId);
            
            return ResponseEntity.ok(queueLength);
            
        } catch (Exception e) {
            log.error("Error getting queue length for business {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{businessId}/wait-time")
    public ResponseEntity<Long> getEstimatedWaitTime(@PathVariable Long businessId) {
        try {
            log.info("Getting estimated wait time for business {}", businessId);
            
            Long estimatedWaitTime = queueService.getEstimatedWaitTime(businessId);
            
            return ResponseEntity.ok(estimatedWaitTime);
            
        } catch (Exception e) {
            log.error("Error getting estimated wait time for business {}: {}", businessId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/entry/{queueEntryId}")
    public ResponseEntity<QueueEntryDto> getQueueEntry(@PathVariable Long queueEntryId) {
        try {
            log.info("Getting queue entry {}", queueEntryId);
            
            QueueEntryDto queueEntry = queueService.getQueueEntryStatus(queueEntryId);
            
            return ResponseEntity.ok(queueEntry);
            
        } catch (QueueEntryNotFoundException e) {
            log.warn("Queue entry not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Error getting queue entry {}: {}", queueEntryId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/customer/{whatsappIdentifier}/business/{businessId}")
    public ResponseEntity<QueueEntryDto> getCustomerQueueEntry(
            @PathVariable String whatsappIdentifier,
            @PathVariable Long businessId) {
        try {
            log.info("Getting queue entry for customer {} in business {}", whatsappIdentifier, businessId);
            
            return queueService.getQueueEntryByWhatsApp(whatsappIdentifier, businessId)
                    .map(queueEntry -> {
                        log.info("Found queue entry for customer {}", whatsappIdentifier);
                        return ResponseEntity.ok(queueEntry);
                    })
                    .orElseGet(() -> {
                        log.info("No active queue entry found for customer {}", whatsappIdentifier);
                        return ResponseEntity.notFound().build();
                    });
            
        } catch (Exception e) {
            log.error("Error getting queue entry for customer {} in business {}: {}", 
                    whatsappIdentifier, businessId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/join")
    public ResponseEntity<QueueEntryDto> joinQueue(@RequestBody JoinQueueRequest request) {
        try {
            log.info("Customer {} joining queue for business {}", 
                    request.getWhatsappIdentifier(), request.getBusinessId());
            
            QueueEntryDto queueEntry = queueService.joinQueue(
                    request.getWhatsappIdentifier(),
                    request.getCustomerName(),
                    request.getBusinessId()
            );
            
            log.info("Customer {} joined queue successfully at position {}", 
                    request.getWhatsappIdentifier(), queueEntry.getPosition());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(queueEntry);
            
        } catch (Exception e) {
            log.error("Error joining queue for customer {} in business {}: {}", 
                    request.getWhatsappIdentifier(), request.getBusinessId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<QueueEntryDto> leaveQueue(@RequestBody LeaveQueueRequest request) {
        try {
            log.info("Customer {} leaving queue for business {}", 
                    request.getWhatsappIdentifier(), request.getBusinessId());
            
            QueueEntryDto queueEntry = queueService.leaveQueue(
                    request.getWhatsappIdentifier(),
                    request.getBusinessId()
            );
            
            log.info("Customer {} left queue successfully", request.getWhatsappIdentifier());
            
            return ResponseEntity.ok(queueEntry);
            
        } catch (QueueEntryNotFoundException e) {
            log.warn("Customer {} not found in queue: {}", request.getWhatsappIdentifier(), e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Error leaving queue for customer {} in business {}: {}", 
                    request.getWhatsappIdentifier(), request.getBusinessId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/entry/{queueEntryId}/position")
    public ResponseEntity<QueueEntryDto> updateQueuePosition(
            @PathVariable Long queueEntryId,
            @RequestBody UpdatePositionRequest request) {
        try {
            log.info("Updating queue entry {} position to {}", queueEntryId, request.getNewPosition());
            
            QueueEntryDto queueEntry = queueService.updateQueuePosition(queueEntryId, request.getNewPosition());
            
            log.info("Updated queue entry {} position successfully", queueEntryId);
            
            return ResponseEntity.ok(queueEntry);
            
        } catch (QueueEntryNotFoundException e) {
            log.warn("Queue entry not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("Error updating queue entry {} position: {}", queueEntryId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request DTOs
    public static class JoinQueueRequest {
        private String whatsappIdentifier;
        private String customerName;
        private Long businessId;

        // Getters and setters
        public String getWhatsappIdentifier() { return whatsappIdentifier; }
        public void setWhatsappIdentifier(String whatsappIdentifier) { this.whatsappIdentifier = whatsappIdentifier; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Long getBusinessId() { return businessId; }
        public void setBusinessId(Long businessId) { this.businessId = businessId; }
    }

    public static class LeaveQueueRequest {
        private String whatsappIdentifier;
        private Long businessId;

        // Getters and setters
        public String getWhatsappIdentifier() { return whatsappIdentifier; }
        public void setWhatsappIdentifier(String whatsappIdentifier) { this.whatsappIdentifier = whatsappIdentifier; }
        public Long getBusinessId() { return businessId; }
        public void setBusinessId(Long businessId) { this.businessId = businessId; }
    }

    public static class UpdatePositionRequest {
        private Integer newPosition;

        // Getters and setters
        public Integer getNewPosition() { return newPosition; }
        public void setNewPosition(Integer newPosition) { this.newPosition = newPosition; }
    }
}
