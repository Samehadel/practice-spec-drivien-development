package com.example.whatsappqueue.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/queue")
public class QueueController {

    // Placeholder for queue management endpoints
    // Will be implemented in User Story 4

    @GetMapping("/{businessId}")
    public String getBusinessQueue(@PathVariable String businessId) {
        return "Get business queue endpoint - to be implemented";
    }

    @PostMapping("/{businessId}/advance")
    public String advanceQueue(@PathVariable String businessId) {
        return "Advance queue endpoint - to be implemented";
    }

    @PostMapping("/{businessId}/skip")
    public String skipCustomer(@PathVariable String businessId) {
        return "Skip customer endpoint - to be implemented";
    }

    @PutMapping("/{businessId}/status")
    public String updateQueueStatus(@PathVariable String businessId) {
        return "Update queue status endpoint - to be implemented";
    }
}
