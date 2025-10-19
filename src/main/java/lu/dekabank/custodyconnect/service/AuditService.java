package lu.dekabank.custodyconnect.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lu.dekabank.custodyconnect.model.AuditLog;
import lu.dekabank.custodyconnect.util.HashUtil;

@ApplicationScoped
public class AuditService {
  @PersistenceContext(unitName="custody-connect") EntityManager em;

  @Transactional public void log(String eventId, String action, String level, String msg) {
    var log = new AuditLog(eventId, action, level, msg);
    computeHashChain(log);
    em.persist(log);
  }

  private void computeHashChain(AuditLog log) {
    // Get the most recent audit log's hash for prevHash
    AuditLog prevLog = em.createQuery(
        "SELECT a FROM AuditLog a ORDER BY a.loggedAt DESC", AuditLog.class)
        .setMaxResults(1)
        .getResultList()
        .stream()
        .findFirst()
        .orElse(null);

    String prevHashVal = prevLog != null ? prevLog.getHash() : "0000000000000000000000000000000000000000000000000000000000000000";

    // Create payload to hash: prevHash + eventId + action + level + message + timestamp
    String payload = String.join("|",
        prevHashVal,
        log.getEventId(),
        log.getAction(),
        log.getLevel(),
        String.valueOf(log.getLoggedAt().toEpochMilli())
    );
    if (log.getMessage() != null) {
      payload += "|" + log.getMessage();
    }

    log.setPrevHash(prevHashVal);
    log.setHash(HashUtil.sha256(payload));
  }

  public List<AuditLog> list(int max) {
    return em.createQuery("select a from AuditLog a order by a.loggedAt desc", AuditLog.class)
      .setMaxResults(max).getResultList();
  }

  public long count() {
    return em.createQuery("select count(a) from AuditLog a", Long.class).getSingleResult();
  }
}
