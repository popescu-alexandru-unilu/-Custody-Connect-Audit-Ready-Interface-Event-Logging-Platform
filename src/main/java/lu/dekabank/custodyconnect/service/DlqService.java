package lu.dekabank.custodyconnect.service;

import java.util.List;

import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

import lu.dekabank.custodyconnect.model.DlqEvent;
import lu.dekabank.custodyconnect.model.OutboxEvent;

@Singleton
public class DlqService {

    @PersistenceContext(unitName = "custody-connect")
    EntityManager em;

    @Inject AuditService audit;

    @Transactional
    public void addToDlq(DlqEvent event) {
        em.persist(event);
    }

    @Transactional
    public void addToDlq(String eventId, String payload, String reason) {
        DlqEvent dlqEvent = new DlqEvent(eventId, payload, reason, null, null);
        em.persist(dlqEvent);
    }

    public List<DlqEvent> getDlqMessages(int limit) {
        return em.createQuery(
            "SELECT d FROM DlqEvent d WHERE d.reprocessable = true ORDER BY d.failedAt",
            DlqEvent.class)
            .setMaxResults(limit)
            .getResultList();
    }

    public List<DlqEvent> getAllDlqMessages() {
        return em.createQuery("SELECT d FROM DlqEvent d ORDER BY d.failedAt DESC", DlqEvent.class)
                .getResultList();
    }

    @Transactional
    public boolean reprocessDlqMessage(Long dlqId, OutboxService outboxService) {
        DlqEvent dlqEvent = em.find(DlqEvent.class, dlqId);
        if (dlqEvent == null || !dlqEvent.isReprocessable()) {
            return false;
        }

        // Create new outbox message for retry
        OutboxEvent retryMessage = new OutboxEvent(
            dlqEvent.getOriginalEventId(),
            dlqEvent.getPayload(),
            dlqEvent.getDestination(),
            dlqEvent.getEventType()
        );

        try {
            outboxService.addToOutbox(retryMessage);

            // Mark as processed (remove from DLQ)
            dlqEvent.setReprocessable(false);
            em.merge(dlqEvent);

            audit.log(dlqEvent.getOriginalEventId(), "DLQ_REPROCESS",
                "INFO", "DLQ message " + dlqId + " reprocessed successfully");
            return true;
        } catch (Exception e) {
            audit.log(dlqEvent.getOriginalEventId(), "DLQ_REPROCESS_FAILED",
                "ERROR", "Failed to reprocess DLQ message " + dlqId + ": " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean deleteDlqMessage(Long dlqId) {
        DlqEvent dlqEvent = em.find(DlqEvent.class, dlqId);
        if (dlqEvent != null) {
            em.remove(dlqEvent);
            audit.log(dlqEvent.getOriginalEventId(), "DLQ_DELETE",
                "INFO", "DLQ message " + dlqId + " deleted");
            return true;
        }
        return false;
    }

    public long getDlqCount() {
        return em.createQuery("SELECT COUNT(d) FROM DlqEvent d WHERE d.reprocessable = true",
                Long.class)
                .getSingleResult();
    }
}
