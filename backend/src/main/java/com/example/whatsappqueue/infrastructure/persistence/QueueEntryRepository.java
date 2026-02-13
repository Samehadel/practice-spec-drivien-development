package com.example.whatsappqueue.infrastructure.persistence;

import com.example.whatsappqueue.domain.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    /**
     * Find active queue entries for a specific business.
     * 
     * @param businessId the business ID to filter by
     * @return List of active queue entries ordered by join time
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.status = 'ACTIVE' ORDER BY qe.joinedAt ASC")
    List<QueueEntry> findActiveEntriesByBusinessId(@Param("businessId") Long businessId);

    /**
     * Find a queue entry by business ID and WhatsApp identifier for active entries only.
     * 
     * @param businessId the business ID
     * @param whatsappIdentifier the WhatsApp identifier
     * @return Optional containing the queue entry if found
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.whatsappIdentifier = :whatsappIdentifier AND qe.status = 'ACTIVE'")
    Optional<QueueEntry> findActiveEntryByBusinessIdAndWhatsappIdentifier(@Param("businessId") Long businessId,
                                                                         @Param("whatsappIdentifier") String whatsappIdentifier);

    /**
     * Find all queue entries for a specific business.
     * 
     * @param businessId the business ID to filter by
     * @return List of all queue entries for the business
     */
    List<QueueEntry> findByBusinessId(Long businessId);

    /**
     * Find queue entries by status for a specific business.
     * 
     * @param businessId the business ID
     * @param status the status to filter by
     * @return List of queue entries with the specified status
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.status = :status ORDER BY qe.joinedAt ASC")
    List<QueueEntry> findByBusinessIdAndStatus(@Param("businessId") Long businessId,
                                               @Param("status") QueueEntry.Status status);

    /**
     * Count active queue entries for a specific business.
     * 
     * @param businessId the business ID
     * @return number of active queue entries
     */
    @Query("SELECT COUNT(qe) FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.status = 'ACTIVE'")
    long countActiveEntriesByBusinessId(@Param("businessId") Long businessId);

    /**
     * Find queue entries that joined after a specific time for a business.
     * 
     * @param businessId the business ID
     * @param joinedAfter the time threshold
     * @return List of queue entries that joined after the specified time
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.joinedAt > :joinedAfter ORDER BY qe.joinedAt ASC")
    List<QueueEntry> findByBusinessIdAndJoinedAtAfter(@Param("businessId") Long businessId,
                                                      @Param("joinedAfter") LocalDateTime joinedAfter);

    /**
     * Find queue entries served within a time range for a business.
     * 
     * @param businessId the business ID
     * @param servedFrom the start of the time range
     * @param servedTo the end of the time range
     * @return List of queue entries served within the time range
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.status = 'SERVED' AND qe.servedAt BETWEEN :servedFrom AND :servedTo ORDER BY qe.servedAt ASC")
    List<QueueEntry> findServedEntriesByBusinessIdAndTimeRange(@Param("businessId") Long businessId,
                                                              @Param("servedFrom") LocalDateTime servedFrom,
                                                              @Param("servedTo") LocalDateTime servedTo);

    /**
     * Check if an active queue entry exists for a business and WhatsApp identifier.
     * 
     * @param businessId the business ID
     * @param whatsappIdentifier the WhatsApp identifier
     * @return true if an active entry exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(qe) > 0 THEN true ELSE false END FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.whatsappIdentifier = :whatsappIdentifier AND qe.status = 'ACTIVE'")
    boolean existsActiveEntryByBusinessIdAndWhatsappIdentifier(@Param("businessId") Long businessId,
                                                             @Param("whatsappIdentifier") String whatsappIdentifier);

    /**
     * Find the next customer to be served (first in active queue) for a business.
     * 
     * @param businessId the business ID
     * @return Optional containing the next queue entry if found
     */
    @Query("SELECT qe FROM QueueEntry qe WHERE qe.business.id = :businessId AND qe.status = 'ACTIVE' ORDER BY qe.joinedAt ASC")
    Optional<QueueEntry> findNextCustomerForBusiness(@Param("businessId") Long businessId);

    /**
     * Find queue entries by business ID, WhatsApp identifier, and status.
     * 
     * @param businessId the business ID
     * @param whatsappIdentifier the WhatsApp identifier
     * @param status the status to filter by
     * @return Optional containing the queue entry if found
     */
    Optional<QueueEntry> findByBusinessIdAndWhatsappIdentifierAndStatus(Long businessId, 
                                                                       String whatsappIdentifier, 
                                                                       QueueEntry.Status status);

    /**
     * Count queue entries by business ID and status.
     * 
     * @param businessId the business ID
     * @param status the status to filter by
     * @return number of queue entries with the specified status
     */
    Integer countByBusinessIdAndStatus(Long businessId, QueueEntry.Status status);

    /**
     * Find queue entries by business ID and status, ordered by position ascending.
     * 
     * @param businessId the business ID
     * @param status the status to filter by
     * @return List of queue entries with the specified status ordered by position
     */
    List<QueueEntry> findByBusinessIdAndStatusOrderByPositionAsc(Long businessId, QueueEntry.Status status);
}
