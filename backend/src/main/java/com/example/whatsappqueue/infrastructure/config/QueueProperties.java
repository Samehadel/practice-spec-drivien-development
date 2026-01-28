package com.example.whatsappqueue.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "queue")
public class QueueProperties {

    private Cache cache = new Cache();
    private Notification notification = new Notification();
    private Eta eta = new Eta();

    public static class Cache {
        private int ttl = 3600; // 1 hour in seconds

        public int getTtl() {
            return ttl;
        }

        public void setTtl(int ttl) {
            this.ttl = ttl;
        }
    }

    public static class Notification {
        private int rateLimit = 30; // seconds between notifications

        public int getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }
    }

    public static class Eta {
        private int defaultServiceTime = 10; // minutes

        public int getDefaultServiceTime() {
            return defaultServiceTime;
        }

        public void setDefaultServiceTime(int defaultServiceTime) {
            this.defaultServiceTime = defaultServiceTime;
        }
    }

    // Getters and Setters
    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Eta getEta() {
        return eta;
    }

    public void setEta(Eta eta) {
        this.eta = eta;
    }
}
