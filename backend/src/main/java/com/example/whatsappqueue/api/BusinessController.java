package com.example.whatsappqueue.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/businesses")
public class BusinessController {

    // Placeholder for business management endpoints
    // Will be implemented in User Story 4

    @GetMapping
    public String listBusinesses() {
        return "Business list endpoint - to be implemented";
    }

    @PostMapping
    public String createBusiness() {
        return "Create business endpoint - to be implemented";
    }

    @GetMapping("/{businessId}")
    public String getBusiness(@PathVariable String businessId) {
        return "Get business endpoint - to be implemented";
    }

    @PutMapping("/{businessId}")
    public String updateBusiness(@PathVariable String businessId) {
        return "Update business endpoint - to be implemented";
    }
}
