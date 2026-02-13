package com.example.whatsappqueue.application;

import com.example.whatsappqueue.application.dto.QueueEntryDto;
import com.example.whatsappqueue.common.exception.BusinessNotFoundException;
import com.example.whatsappqueue.common.exception.QueueClosedException;
import com.example.whatsappqueue.common.exception.QueueEntryNotFoundException;
import com.example.whatsappqueue.domain.Business;
import com.example.whatsappqueue.domain.QueueEntry;
import com.example.whatsappqueue.infrastructure.mapper.QueueEntryMapper;
import com.example.whatsappqueue.infrastructure.persistence.BusinessRepository;
import com.example.whatsappqueue.infrastructure.persistence.QueueEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final BusinessRepository businessRepository;
    private final QueueEntryMapper queueEntryMapper;

    public QueueEntryDto joinQueue(String whatsappIdentifier, String customerName, Long businessId) {
        log.info("Customer {} attempting to join queue for business {}", whatsappIdentifier, businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with id: " + businessId));

        if (!business.getQueueOpen()) {
            throw new QueueClosedException("Queue is closed for business: " + business.getName());
        }

        // Check if customer is already in queue
        Optional<QueueEntry> existingEntry = queueEntryRepository
                .findByBusinessIdAndWhatsappIdentifierAndStatus(businessId, whatsappIdentifier, QueueEntry.Status.ACTIVE);
        
        if (existingEntry.isPresent()) {
            log.warn("Customer {} is already in queue for business {}", whatsappIdentifier, businessId);
            return queueEntryMapper.toDto(existingEntry.get());
        }

        // Calculate position
        Integer position = queueEntryRepository.countByBusinessIdAndStatus(businessId, QueueEntry.Status.ACTIVE) + 1;

        QueueEntry queueEntry = QueueEntry.builder()
                .whatsappIdentifier(whatsappIdentifier)
                .customerName(customerName)
                .business(business)
                .status(QueueEntry.Status.ACTIVE)
                .position(position)
                .joinedAt(LocalDateTime.now())
                .build();

        QueueEntry savedEntry = queueEntryRepository.save(queueEntry);
        log.info("Customer {} joined queue for business {} at position {}", whatsappIdentifier, businessId, position);

        return queueEntryMapper.toDto(savedEntry);
    }

    @Transactional(readOnly = true)
    public QueueEntryDto getQueueEntryStatus(Long queueEntryId) {
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new QueueEntryNotFoundException("Queue entry not found with id: " + queueEntryId));

        return queueEntryMapper.toDto(queueEntry);
    }

    @Transactional(readOnly = true)
    public Optional<QueueEntryDto> getQueueEntryByWhatsApp(String whatsappIdentifier, Long businessId) {
        return queueEntryRepository
                .findByBusinessIdAndWhatsappIdentifierAndStatus(businessId, whatsappIdentifier, QueueEntry.Status.ACTIVE)
                .map(queueEntryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<QueueEntryDto> getActiveQueueEntries(Long businessId) {
        List<QueueEntry> activeEntries = queueEntryRepository
                .findByBusinessIdAndStatusOrderByPositionAsc(businessId, QueueEntry.Status.ACTIVE);
        
        return queueEntryMapper.toDtoList(activeEntries);
    }

    public QueueEntryDto updateQueuePosition(Long queueEntryId, Integer newPosition) {
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new QueueEntryNotFoundException("Queue entry not found with id: " + queueEntryId));

        Integer oldPosition = queueEntry.getPosition();
        queueEntry.setPosition(newPosition);
        
        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        log.info("Updated queue entry {} position from {} to {}", queueEntryId, oldPosition, newPosition);

        return queueEntryMapper.toDto(updatedEntry);
    }

    public QueueEntryDto markAsServed(Long queueEntryId) {
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new QueueEntryNotFoundException("Queue entry not found with id: " + queueEntryId));

        queueEntry.setStatus(QueueEntry.Status.SERVED);
        queueEntry.setServedAt(LocalDateTime.now());
        queueEntry.setPosition(null);

        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        log.info("Marked queue entry {} as served", queueEntryId);

        // Recalculate positions for remaining active entries
        recalculatePositions(queueEntry.getBusiness().getId());

        return queueEntryMapper.toDto(updatedEntry);
    }

    public QueueEntryDto markAsNoShow(Long queueEntryId) {
        QueueEntry queueEntry = queueEntryRepository.findById(queueEntryId)
                .orElseThrow(() -> new QueueEntryNotFoundException("Queue entry not found with id: " + queueEntryId));

        queueEntry.setStatus(QueueEntry.Status.NO_SHOW);
        queueEntry.setPosition(null);

        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        log.info("Marked queue entry {} as no-show", queueEntryId);

        // Recalculate positions for remaining active entries
        recalculatePositions(queueEntry.getBusiness().getId());

        return queueEntryMapper.toDto(updatedEntry);
    }

    public QueueEntryDto leaveQueue(String whatsappIdentifier, Long businessId) {
        QueueEntry queueEntry = queueEntryRepository
                .findByBusinessIdAndWhatsappIdentifierAndStatus(businessId, whatsappIdentifier, QueueEntry.Status.ACTIVE)
                .orElseThrow(() -> new QueueEntryNotFoundException("Active queue entry not found for customer: " + whatsappIdentifier));

        queueEntry.setStatus(QueueEntry.Status.LEFT);
        queueEntry.setPosition(null);

        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        log.info("Customer {} left queue for business {}", whatsappIdentifier, businessId);

        // Recalculate positions for remaining active entries
        recalculatePositions(businessId);

        return queueEntryMapper.toDto(updatedEntry);
    }

    private void recalculatePositions(Long businessId) {
        List<QueueEntry> activeEntries = queueEntryRepository
                .findByBusinessIdAndStatusOrderByPositionAsc(businessId, QueueEntry.Status.ACTIVE);

        for (int i = 0; i < activeEntries.size(); i++) {
            QueueEntry entry = activeEntries.get(i);
            entry.setPosition(i + 1);
            queueEntryRepository.save(entry);
        }

        log.info("Recalculated positions for {} active entries in business {}", activeEntries.size(), businessId);
    }

    @Transactional(readOnly = true)
    public Integer getQueueLength(Long businessId) {
        return queueEntryRepository.countByBusinessIdAndStatus(businessId, QueueEntry.Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Long getEstimatedWaitTime(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessNotFoundException("Business not found with id: " + businessId));

        Integer queueLength = getQueueLength(businessId);
        return (long) (queueLength * business.getAverageServiceTimeMinutes());
    }
}
