package com.example.whatsappqueue.infrastructure.persistence;

import com.example.whatsappqueue.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    /**
     * Find a business by its WhatsApp phone number.
     * 
     * @param whatsappPhoneNumber the WhatsApp phone number to search for
     * @return Optional containing the business if found
     */
    Optional<Business> findByWhatsappPhoneNumber(String whatsappPhoneNumber);

    /**
     * Check if a business exists with the given WhatsApp phone number.
     * 
     * @param whatsappPhoneNumber the WhatsApp phone number to check
     * @return true if a business exists with the phone number, false otherwise
     */
    boolean existsByWhatsappPhoneNumber(String whatsappPhoneNumber);

    /**
     * Find all businesses with queue open status.
     * 
     * @return Iterable of businesses with open queues
     */
    @Query("SELECT b FROM Business b WHERE b.queueOpen = true")
    Iterable<Business> findAllWithQueueOpen();

    /**
     * Find businesses by service type.
     * 
     * @param serviceType the service type to filter by
     * @return Iterable of businesses matching the service type
     */
    Iterable<Business> findByServiceType(String serviceType);

    /**
     * Find businesses by service type and queue open status.
     * 
     * @param serviceType the service type to filter by
     * @param queueOpen the queue open status to filter by
     * @return Iterable of businesses matching both criteria
     */
    @Query("SELECT b FROM Business b WHERE b.serviceType = :serviceType AND b.queueOpen = :queueOpen")
    Iterable<Business> findByServiceTypeAndQueueOpen(@Param("serviceType") String serviceType, 
                                                   @Param("queueOpen") Boolean queueOpen);

    /**
     * Count businesses with queue open status.
     * 
     * @return number of businesses with open queues
     */
    @Query("SELECT COUNT(b) FROM Business b WHERE b.queueOpen = true")
    long countByQueueOpen();
}
