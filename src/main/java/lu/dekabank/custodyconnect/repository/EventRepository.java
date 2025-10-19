package lu.dekabank.custodyconnect.repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lu.dekabank.custodyconnect.model.Event;

@ApplicationScoped
public class EventRepository {

  @PersistenceContext(unitName = "custody-connect")
  EntityManager em;

  public void save(Event e) { em.persist(e); }

  public Event find(String id) { return em.find(Event.class, id); }

  public List<Event> list(int max) {
    return em.createQuery("select e from Event e order by e.eventTimestamp desc", Event.class)
             .setMaxResults(max).getResultList();
  }

  public List<Event> search(String status, String type, String source,
                            Instant from, Instant to, String q,
                            int page, int size) {
    StringBuilder jpql = new StringBuilder("select e from Event e where 1=1");
    Map<String,Object> p = new HashMap<>();
    if (status != null && !status.isBlank()) { jpql.append(" and e.status = :status"); p.put("status", status); }
    if (type   != null && !type.isBlank())   { jpql.append(" and e.type = :type");     p.put("type", type); }
    if (source != null && !source.isBlank()) { jpql.append(" and e.sourceSystem = :src"); p.put("src", source); }
    if (from != null) { jpql.append(" and e.eventTimestamp >= :from"); p.put("from", from); }
    if (to   != null) { jpql.append(" and e.eventTimestamp <= :to");   p.put("to", to); }
    if (q != null && !q.isBlank()) {
      jpql.append(" and (lower(e.type) like :q or lower(e.sourceSystem) like :q or lower(e.status) like :q or e.id like :q)");
      p.put("q", "%"+q.toLowerCase()+"%");
    }
    jpql.append(" order by e.eventTimestamp desc");

    var query = em.createQuery(jpql.toString(), Event.class);
    p.forEach(query::setParameter);
    query.setFirstResult(Math.max(0, page) * Math.max(1, size));
    query.setMaxResults(Math.max(1, size));
    return query.getResultList();
  }

  public long count(String status, String type, String source, Instant from, Instant to, String q) {
    StringBuilder jpql = new StringBuilder("select count(e) from Event e where 1=1");
    Map<String,Object> p = new HashMap<>();
    if (status != null && !status.isBlank()) { jpql.append(" and e.status = :status"); p.put("status", status); }
    if (type   != null && !type.isBlank())   { jpql.append(" and e.type = :type");     p.put("type", type); }
    if (source != null && !source.isBlank()) { jpql.append(" and e.sourceSystem = :src"); p.put("src", source); }
    if (from != null) { jpql.append(" and e.eventTimestamp >= :from"); p.put("from", from); }
    if (to   != null) { jpql.append(" and e.eventTimestamp <= :to");   p.put("to", to); }
    if (q != null && !q.isBlank()) {
      jpql.append(" and (lower(e.type) like :q or lower(e.sourceSystem) like :q or lower(e.status) like :q or e.id like :q)");
      p.put("q", "%"+q.toLowerCase()+"%");
    }
    var query = em.createQuery(jpql.toString(), Long.class);
    p.forEach(query::setParameter);
    return query.getSingleResult();
  }

  // Cursor-based pagination using (eventTimestamp,id) as cursor
  public List<Event> searchCursor(String status, String type, String source,
                                  Instant from, Instant to, String q,
                                  String cursor, int limit) {
    StringBuilder jpql = new StringBuilder("select e from Event e where 1=1");
    Map<String,Object> p = new HashMap<>();

    // Decode cursor
    Instant cursorTimestamp = null;
    String cursorId = null;
    if (cursor != null && !cursor.isBlank()) {
      try {
        String decoded = new String(Base64.getDecoder().decode(cursor));
        String[] parts = decoded.split("\\|");
        if (parts.length == 2) {
          cursorTimestamp = Instant.parse(parts[0]);
          cursorId = parts[1];
        }
      } catch (Exception e) {
        // Invalid cursor, ignore
      }
    }

    // Add filters
    if (status != null && !status.isBlank()) { jpql.append(" and e.status = :status"); p.put("status", status); }
    if (type   != null && !type.isBlank())   { jpql.append(" and e.type = :type");     p.put("type", type); }
    if (source != null && !source.isBlank()) { jpql.append(" and e.sourceSystem = :src"); p.put("src", source); }
    if (from != null) { jpql.append(" and e.eventTimestamp >= :from"); p.put("from", from); }
    if (to   != null) { jpql.append(" and e.eventTimestamp <= :to");   p.put("to", to); }
    if (q != null && !q.isBlank()) {
      jpql.append(" and (lower(e.type) like :q or lower(e.sourceSystem) like :q or lower(e.status) like :q or e.id like :q)");
      p.put("q", "%"+q.toLowerCase()+"%");
    }

    // Add cursor condition
    if (cursorTimestamp != null && cursorId != null) {
      jpql.append(" and (e.eventTimestamp < :cursorTs or (e.eventTimestamp = :cursorTs and e.id > :cursorId))");
      p.put("cursorTs", cursorTimestamp);
      p.put("cursorId", cursorId);
    }

    jpql.append(" order by e.eventTimestamp desc, e.id asc");

    var query = em.createQuery(jpql.toString(), Event.class);
    p.forEach(query::setParameter);
    query.setMaxResults(Math.max(1, Math.min(100, limit))); // Max 100 per page
    return query.getResultList();
  }

  // Helper to encode next cursor from last event
  public String encodeCursor(Event lastEvent) {
    if (lastEvent == null) return null;
    String cursor = lastEvent.getEventTimestamp().toString() + "|" + lastEvent.getId();
    return Base64.getEncoder().encodeToString(cursor.getBytes());
  }
}
