package com.example.whatsappqueue.infrastructure.persistence;

import com.example.whatsappqueue.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications by business ID.
     * 
     * @param businessId the business ID to filter by
     * @return List of notifications for the business ordered by sent time
     */
    List<Notification> findByBusinessIdOrderBySentAtDesc(Long businessId);

    /**
     * Find notifications by queue entry ID.
     * 
     * @param queueEntryId the queue entry ID to filter by
     * @return List of notifications for the queue entry ordered by sent time
     */
    List<Notification> findByQueueEntryIdOrderBySentAtDesc(Long queueEntryId);

    /**
     * Find notifications by business ID and message type.
     * 
     * @param businessId the business ID
     * @param messageType the message type to filter by
     * @return List of notifications matching the criteria
     */
    @Query("SELECT n FROM Notification n WHERE n.businessId = :businessId AND n.messageType = :messageType ORDER BY n.sentAt DESC")
    List<Notification> findByBusinessIdAndMessageType(@Param("businessId") Long businessId,
                                                        @Param("messageType") Notification.MessageType messageType);

    /**
     * Find notifications by status.
     * 
     * @param status the status to filter by
     * @return List of notifications with the specified status
     */
    List<Notification> findByStatus(Notification.Status status);

    /**
     * Find notifications by business ID and status.
     * 
     * @param businessId the business ID
     * @param status the status to filter by
     * @return List of notifications matching the criteria
     */
    @Query("SELECT n FROM Notification n WHERE n.businessId = :businessId AND n.status = :status ORDER BY n.sentAt ASC")
    List<Notification> findByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                                  @Param("status") Notification.Status status);

    /**
     * Find pending notifications for a business.
     * 
     * @param businessId the business ID
     * @return List of pending notifications ordered by sent time
     */
    @Query("SELECT n FROM Notification n WHERE n.businessId = :businessId AND n.status = 'PENDING' ORDER BY n.sentAt ASC")
    List<Notification> findPendingNotificationsByBusinessId(@Param("businessId") Long businessId);

    /**
     * Find failed notifications for a business.
     * 
     * @param businessId the business ID
     * @return List of failed notifications ordered by sent time
     */
    @Query("SELECT n FROM Notification n WHERE n.businessId = :businessId AND n.status = 'FAILED' ORDER BY n.sentAt DESC")
    List<Notification> findFailedNotificationsByBusinessId(@Param("businessId") Long businessId);

    /**
     * Find notifications sent within a time range for a business.
     * 
     * @param businessId the business ID
     * @param sentFrom the start of the time range
     * @param sentTo the end of the time range
     * @return List of notifications sent within the time range
     */
    @Query("SELECT n FROM Notification n WHERE n.businessId = :businessId AND n.sentAt BETWEEN :sentFrom AND :sentTo ORDER BY n.sentAt DESC")
    List<Notification> findByBusinessIdAndSentAtBetween(@Param("businessId") Long businessId,
                                                        @Param("sentFrom") LocalDateTime sentFrom,
                                                        @Param("sentTo") LocalDateTime sentTo);

    /**
     * Count notifications by business ID and status.
     * 
     * @param businessId the business ID
     * @param status the status to count
     * @return number of notifications with the specified status
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.businessId = :businessId AND n.status = :status")
    long countByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                   @Param("status") Notification.Status status);

    /**
     * Count notifications by message type for a business.
     * 
     * @param businessId the business ID
     * @param messageType the message type to count
     * @return number of notifications of the specified type
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.businessId = :businessId AND n.messageType = :messageType")
    long countByBusinessIdAndMessageType(@Param("businessId") Long businessId,
                                        @Param("messageType") Notification.MessageType messageType);

    /**
     * Find the most recent notification for a queue entry.
     * 
     * @param queueEntryId the queue entry ID
     * @return Optional containing the most recent notification if found
     */
    @Query("SELECT n FROM Notification n WHERE n.queueEntryId = :queueEntryId ORDER BY n.sentAt DESC")
    List<Notification> findMostRecentNotificationForQueueEntry(@Param("queueEntryId") Long queueEntryId);

    /**
     * Find notifications that need to be retried (failed notifications sent recently).
     * 
     * @param sentAfter only consider notifications sent after this time
     * @return List of failed notifications that may need retry
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.sentAt > :sentAfter ORDER BY n.sentAt ASC")
    List<Notification> findRetryableNotifications(@Param("sentAfter") LocalDateTime sentAfter);
}
