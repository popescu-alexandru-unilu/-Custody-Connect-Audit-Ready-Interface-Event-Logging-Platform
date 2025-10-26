package lu.bank.custodyconnect.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lu.bank.custodyconnect.model.Event;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventQueryService {
  @PersistenceContext(unitName="custody-connect") EntityManager em;

  public List<Event> search(String status, String type, String source,
                             Instant from, Instant to, String q, int page, int size) {
    return search(status, type, source, from, to, q, page, size, null, null);
  }

  public List<Event> search(String status, String type, String source,
                             Instant from, Instant to, String q, int page, int size,
                             String sort, String dir) {
    StringBuilder jpql = new StringBuilder("select e from Event e where 1=1");
    Map<String,Object> params = new HashMap<>();
    if (status != null && !status.isBlank()) { jpql.append(" and e.status = :status"); params.put("status", status); }
    if (type   != null && !type.isBlank())   { jpql.append(" and e.type = :type");     params.put("type", type); }
    if (source != null && !source.isBlank()) { jpql.append(" and e.sourceSystem = :src"); params.put("src", source); }
    if (from != null) { jpql.append(" and e.eventTimestamp >= :from"); params.put("from", from); }
    if (to   != null) { jpql.append(" and e.eventTimestamp <= :to");   params.put("to", to); }
    if (q != null && !q.isBlank()) {
      jpql.append(" and (lower(e.type) like :q or lower(e.sourceSystem) like :q or lower(e.status) like :q or e.id like :q)");
      params.put("q", "%"+q.toLowerCase()+"%");
    }
    // Sorting
    String s = sort != null ? sort.trim().toLowerCase() : "";
    String d = dir != null ? dir.trim().toLowerCase() : "desc";
    String order;
    switch (s) {
      case "id": order = "e.id"; break;
      case "type": order = "e.type"; break;
      case "source": case "sourcesystem": order = "e.sourceSystem"; break;
      case "status": order = "e.status"; break;
      case "time": case "eventtimestamp": order = "e.eventTimestamp"; break;
      default: order = "e.eventTimestamp"; d = "desc"; break;
    }
    String direction = ("asc".equals(d) ? "asc" : "desc");
    jpql.append(" order by ").append(order).append(' ').append(direction);

    var query = em.createQuery(jpql.toString(), Event.class);
    params.forEach(query::setParameter);

    int first = Math.max(0, page) * Math.max(1, size);
    query.setFirstResult(first);
    query.setMaxResults(Math.max(1, size));

    List<Event> pageItems = query.getResultList();
    return pageItems;
  }

  public String exportCsv(String status, String type, String source, Instant from, Instant to, String q) {
    var pageItems = search(status, type, source, from, to, q, 0, 10000, "eventTimestamp", "desc");
    var data = pageItems.stream().map(this::toMap).collect(Collectors.toList());
    String header = "id,type,sourceSystem,status,eventTimestamp,payload";
    var lines = new ArrayList<String>();
    lines.add(header);
    for (var m : data) {
      lines.add(String.join(",",
          csv(m.get("id")),
          csv(m.get("type")),
          csv(m.get("sourceSystem")),
          csv(m.get("status")),
          csv(m.get("eventTimestamp")),
          csv(m.get("payload"))
      ));
    }
    return String.join("\n", lines);
  }

  private String csv(Object o) {
    if (o == null) return "";
    String s = String.valueOf(o);
    s = s.replace("\"","\"\"");
    return "\""+s+"\"";
  }

  private Map<String,Object> toMap(Event e) {
    return Map.of(
      "id", e.getId(),
      "type", e.getType(),
      "sourceSystem", e.getSourceSystem(),
      "status", e.getStatus(),
      "eventTimestamp", e.getEventTimestamp(),
      "payload", e.getPayload()
    );
  }
}
