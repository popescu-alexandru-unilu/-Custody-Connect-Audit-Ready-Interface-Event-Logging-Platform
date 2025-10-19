package lu.dekabank.custodyconnect.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lu.dekabank.custodyconnect.model.AuditLog;
import lu.dekabank.custodyconnect.model.Event;

@Singleton
public class RetentionService {

    @PersistenceContext(unitName = "custody-connect")
    EntityManager em;

    @Inject AuditService audit;

    // Run daily at 2 AM for retention cleanup
    @Schedule(hour = "2", minute = "0", second = "0", dayOfWeek = "*", persistent = false)
    public void performRetention() {
        try {
            Instant cutoffDate = Instant.now().minus(365, ChronoUnit.DAYS); // 1 year retention

            int eventsDeleted = deleteOldEvents(cutoffDate);
            int auditLogsDeleted = deleteOldAuditLogs(cutoffDate);
            int reconResultsDeleted = deleteOldReconciliationResults(cutoffDate);

            audit.log(null, "RETENTION_JOB", "INFO",
                String.format("Retention cleanup completed: %d events, %d audit logs, %d reconciliation results deleted before %s",
                    eventsDeleted, auditLogsDeleted, reconResultsDeleted, cutoffDate));

        } catch (Exception e) {
            audit.log(null, "RETENTION_JOB", "ERROR",
                "Retention job failed: " + e.getMessage());
        }
    }

    @Transactional
    public int deleteOldEvents(Instant cutoffDate) {
        // Only delete fully processed/reconciled events older than cutoff
        List<Event> oldEvents = em.createQuery(
            "SELECT e FROM Event e WHERE e.eventTimestamp < :cutoff AND e.status = 'reconciled'", Event.class)
            .setParameter("cutoff", cutoffDate)
            .getResultList();

        for (Event event : oldEvents) {
            em.remove(event);
        }

        return oldEvents.size();
    }

    @Transactional
    public int deleteOldAuditLogs(Instant cutoffDate) {
        // Delete audit logs for events that no longer exist or are very old
        List<AuditLog> oldLogs = em.createQuery(
            "SELECT a FROM AuditLog a WHERE a.loggedAt < :cutoff", AuditLog.class)
            .setParameter("cutoff", cutoffDate)
            .getResultList();

        for (AuditLog log : oldLogs) {
            em.remove(log);
        }

        return oldLogs.size();
    }

    @Transactional
    public int deleteOldReconciliationResults(Instant cutoffDate) {
        // Clean up old reconciliation results (keep for 6 months instead of 1 year)
        Instant reconCutoff = Instant.now().minus(180, ChronoUnit.DAYS);

        int deleted = em.createQuery(
            "DELETE FROM ReconciliationResult r WHERE r.timestamp < :cutoff")
            .setParameter("cutoff", reconCutoff)
            .executeUpdate();

        return deleted;
    }

    // Manual trigger endpoint for admin operations
    @Transactional
    public RetentionResult runRetentionNow() {
        Instant cutoffDate = Instant.now().minus(365, ChronoUnit.DAYS);

        int eventsDeleted = deleteOldEvents(cutoffDate);
        int auditLogsDeleted = deleteOldAuditLogs(cutoffDate);
        int reconResultsDeleted = deleteOldReconciliationResults(cutoffDate);

        return new RetentionResult(eventsDeleted, auditLogsDeleted, reconResultsDeleted, cutoffDate);
    }

    public record RetentionResult(int eventsDeleted, int auditLogsDeleted, int reconResultsDeleted, Instant cutoffDate) {}
}
