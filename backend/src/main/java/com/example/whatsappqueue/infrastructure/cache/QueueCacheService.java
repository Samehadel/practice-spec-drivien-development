package com.example.whatsappqueue.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.whatsappqueue.application.QueueLoggingService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class QueueCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final QueueLoggingService queueLoggingService;
    
    private static final String QUEUE_KEY_PREFIX = "queue:";
    private static final String POSITION_KEY_PREFIX = "position:";
    private static final String CONFIG_KEY_PREFIX = "config:";
    
    /**
     * Adds customer to the queue in Redis
     */
    public void addToQueue(String businessId, String queueEntryId, LocalDateTime joinedAt) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        
        queueLoggingService.logCustomerJoinedQueue(businessId, queueEntryId, getCustomerPosition(businessId, queueEntryId));
        
        // Add to sorted set with timestamp as score for FIFO ordering
        redisTemplate.opsForZSet().add(queueKey, queueEntryId, joinedAt.toEpochSecond(ZoneOffset.UTC));
    }
    
    /**
     * Removes customer from the queue in Redis
     */
    public void removeFromQueue(String businessId, String queueEntryId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        
        queueLoggingService.logCustomerLeftQueue(businessId, queueEntryId);
        redisTemplate.opsForZSet().remove(queueKey, queueEntryId);
        
        // Remove position cache
        String positionKey = POSITION_KEY_PREFIX + businessId + ":" + queueEntryId;
        redisTemplate.delete(positionKey);
    }
    
    /**
     * Gets current position of customer in queue
     */
    public Integer getCustomerPosition(String businessId, String queueEntryId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        String positionKey = POSITION_KEY_PREFIX + businessId + ":" + queueEntryId;
        
        Long position = redisTemplate.opsForZSet().rank(queueKey, queueEntryId);
        if (position != null) {
            // Cache the position for faster access
            redisTemplate.opsForValue().set(positionKey, String.valueOf(position + 1), 1, TimeUnit.HOURS);
            return (int) (position + 1); // Convert to 1-based indexing
        }
        return null;
    }
    
    /**
     * Gets all active customers in queue (ordered by join time)
     */
    public Set<String> getActiveQueueEntries(String businessId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        
        // Get all entries ordered by score (timestamp)
        Set<Object> entries = redisTemplate.opsForZSet().range(queueKey, 0, -1);
        log.debug("Active queue entries for business {}: {}", businessId, entries.size());
        
        return entries.stream()
                .map(entry -> entry.toString())
                .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * Gets queue size for business
     */
    public Integer getQueueSize(String businessId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        Long size = redisTemplate.opsForZSet().size(queueKey);
        return size != null ? size.intValue() : 0;
    }
    
    /**
     * Updates all positions when queue changes
     */
    public void updateAllPositions(String businessId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        Set<Object> entries = redisTemplate.opsForZSet().range(queueKey, 0, -1);
        
        int position = 1;
        for (Object entryId : entries) {
            String positionKey = POSITION_KEY_PREFIX + businessId + ":" + entryId.toString();
            redisTemplate.opsForValue().set(positionKey, String.valueOf(position), 1, TimeUnit.HOURS);
            position++;
        }
        
        log.debug("Updated positions for business {}: {} entries repositioned", businessId, entries.size());
    }
    
    /**
     * Caches business configuration
     */
    public void cacheBusinessConfig(String businessId, Boolean queueOpen, Integer averageServiceTime, Integer notificationThreshold) {
        String configKey = CONFIG_KEY_PREFIX + businessId;
        String configValue = String.format("%s,%s,%s", queueOpen, averageServiceTime, notificationThreshold);
        
        redisTemplate.opsForValue().set(configKey, configValue, 2, TimeUnit.HOURS);
        log.debug("Cached business config for business {}: {}", businessId, configValue);
    }
    
    /**
     * Gets cached business configuration
     */
    public BusinessConfig getBusinessConfig(String businessId) {
        String configKey = CONFIG_KEY_PREFIX + businessId;
        String configValue = (String) redisTemplate.opsForValue().get(configKey);
        
        if (configValue != null) {
            String[] parts = configValue.split(",");
            return BusinessConfig.builder()
                    .queueOpen(Boolean.parseBoolean(parts[0]))
                    .averageServiceTime(Integer.parseInt(parts[1]))
                    .notificationThreshold(Integer.parseInt(parts[2]))
                    .build();
        }
        
        // Return default config if not cached
        return BusinessConfig.builder()
                .queueOpen(true)
                .averageServiceTime(10)
                .notificationThreshold(3)
                .build();
    }
    
    /**
     * Clears queue cache for business
     */
    public void clearQueueCache(String businessId) {
        String queueKey = QUEUE_KEY_PREFIX + businessId;
        redisTemplate.delete(queueKey);
        log.info("Cleared queue cache for business {}", businessId);
    }
    
    /**
     * DTO for business configuration
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessConfig {
        @Builder.Default
        private Boolean queueOpen = true;
        @Builder.Default
        private Integer averageServiceTime = 10;
        @Builder.Default
        private Integer notificationThreshold = 3;
    }
}
