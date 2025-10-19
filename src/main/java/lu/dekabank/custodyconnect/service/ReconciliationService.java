package lu.dekabank.custodyconnect.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lu.dekabank.custodyconnect.model.AuditLog;
import lu.dekabank.custodyconnect.model.Event;
import lu.dekabank.custodyconnect.model.dto.EventDto;
import lu.dekabank.custodyconnect.repository.AuditLogRepository;
import lu.dekabank.custodyconnect.repository.EventRepository;

@ApplicationScoped
public class ReconciliationService {

  @Inject EventRepository events;
  @Inject AuditLogRepository audits;

  @Transactional
  public Event ingest(EventDto dto) {
    var e = new Event();
    e.setType(dto.type);
    e.setSourceSystem(dto.sourceSystem);
    e.setPayload(dto.payload);
    e.setStatus((dto.payload != null && dto.payload.contains("\"match\":true")) ? "match" : "mismatch");
    events.save(e);
    audits.save(new AuditLog(e.getId(), "RECEIVED", "INFO", "Ingested event"));
    audits.save(new AuditLog(e.getId(), e.getStatus().equals("match")? "RECON_MATCH":"RECON_MISMATCH", "INFO", "MVP recon"));
    return e;
  }

  public List<Event> list(int max) { return events.list(max); }
}
