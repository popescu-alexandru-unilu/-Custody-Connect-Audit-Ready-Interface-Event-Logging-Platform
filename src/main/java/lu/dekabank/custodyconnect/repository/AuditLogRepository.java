package lu.dekabank.custodyconnect.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lu.dekabank.custodyconnect.model.AuditLog;

@ApplicationScoped
public class AuditLogRepository {

  @PersistenceContext(unitName = "custody-connect")
  EntityManager em;

  public void save(AuditLog a) { em.persist(a); }

  public List<AuditLog> list(int max) {
    return em.createQuery("select a from AuditLog a order by a.loggedAt desc", AuditLog.class)
             .setMaxResults(max).getResultList();
  }

  public List<AuditLog> byEvent(String eventId, int max) {
    return em.createQuery("select a from AuditLog a where a.eventId = :id order by a.loggedAt", AuditLog.class)
             .setParameter("id", eventId)
             .setMaxResults(max).getResultList();
  }
}
