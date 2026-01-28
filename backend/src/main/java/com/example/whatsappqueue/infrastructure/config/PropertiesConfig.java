package com.example.whatsappqueue.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan({
    "com.example.whatsappqueue.infrastructure.config"
})
public class PropertiesConfig {
    // This class enables @ConfigurationProperties scanning
}
