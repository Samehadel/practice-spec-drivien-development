package com.example.whatsappqueue.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    // Placeholder for WhatsApp webhook endpoints
    // Will be implemented in User Story 1

    @GetMapping
    public String verifyWebhook() {
        return "Webhook verification endpoint - to be implemented";
    }

    @PostMapping
    public String handleWebhook(@RequestBody String payload) {
        return "Handle webhook endpoint - to be implemented";
    }
}
