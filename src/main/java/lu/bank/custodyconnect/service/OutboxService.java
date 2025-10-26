package lu.bank.custodyconnect.service;

import java.time.Instant;
import java.util.List;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lu.bank.custodyconnect.model.DlqEvent;
import lu.bank.custodyconnect.model.OutboxEvent;

@Singleton
public class OutboxService {

    @PersistenceContext(unitName = "custody-connect")
    EntityManager em;

    @Inject AuditService audit;
    @Inject DlqService dlqService;

    // Scheduled job to process outbox messages every minute
    @Schedule(hour = "*", minute = "*", second = "30", persistent = false)
    public void processOutboxMessages() {
        List<OutboxEvent> pendingMessages = em.createQuery(
            "SELECT o FROM OutboxEvent o WHERE o.status = :status ORDER BY o.createdAt",
            OutboxEvent.class)
            .setParameter("status", OutboxEvent.Status.PENDING)
            .setMaxResults(10) // Process in batches
            .getResultList();

        for (OutboxEvent message : pendingMessages) {
            processSingleMessage(message);
        }
    }

    @Transactional
    public void processSingleMessage(OutboxEvent message) {
        try {
            // Mark as processing
            message.setStatus(OutboxEvent.Status.PROCESSING);
            message.setRetryCount(message.getRetryCount() + 1);
            em.merge(message);

            // Simulate message publishing (HTTP call, JMS, etc.)
            boolean success = publishMessage(message);

            if (success) {
                message.setStatus(OutboxEvent.Status.COMPLETED);
                message.setProcessedAt(Instant.now());
                em.merge(message);

                audit.log(message.getEventId(), "OUTBOX_SUCCESS",
                    "INFO", "Message published to " + message.getDestination());
            } else {
                handlePublishFailure(message);
            }

        } catch (Exception e) {
            handlePublishFailure(message);
        }
    }

    private boolean publishMessage(OutboxEvent message) {
        // This is where you would integrate with actual messaging systems
        // For now, simulate success/failure randomly
        // In production: HTTP calls, JMS, Kafka, etc.

        if (message.getDestination().contains("RECON_SERVICE")) {
            // Simulate occasional failures for testing
            return Math.random() > 0.1; // 90% success rate
        }

        return true;
    }

    private void handlePublishFailure(OutboxEvent message) {
        message.setStatus(OutboxEvent.Status.FAILED);
        message.setErrorMessage("Failed to publish message to " + message.getDestination());

        if (message.shouldRetry()) {
            // Reset to pending for retry
            message.setStatus(OutboxEvent.Status.PENDING);
            message.setErrorMessage(null);
            em.merge(message);
        } else {
            // Move to DLQ
            DlqEvent dlqEvent = new DlqEvent(message,
                "Max retries exceeded for " + message.getDestination(),
                "Failed after " + message.getMaxRetries() + " attempts");

            dlqService.addToDlq(dlqEvent);

            // Remove from outbox
            em.remove(message);

            audit.log(message.getEventId(), "OUTBOX_TO_DLQ", "WARN",
                "Message moved to DLQ after " + message.getMaxRetries() + " retries");
        }
    }

    @Transactional
    public void addToOutbox(OutboxEvent message) {
        em.persist(message);
    }

    @Transactional
    public void addToOutbox(String eventId, String payload, String destination, String eventType) {
        OutboxEvent message = new OutboxEvent(eventId, payload, destination, eventType);
        em.persist(message);
    }

    public List<OutboxEvent> getPendingMessages() {
        return em.createQuery("SELECT o FROM OutboxEvent o WHERE o.status = :status",
                OutboxEvent.class)
                .setParameter("status", OutboxEvent.Status.PENDING)
                .getResultList();
    }

    public List<OutboxEvent> getFailedMessages() {
        return em.createQuery("SELECT o FROM OutboxEvent o WHERE o.status = :status",
                OutboxEvent.class)
                .setParameter("status", OutboxEvent.Status.FAILED)
                .getResultList();
    }
}
