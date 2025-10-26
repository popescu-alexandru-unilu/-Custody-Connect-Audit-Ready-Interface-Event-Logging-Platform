package lu.bank.custodyconnect.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lu.bank.custodyconnect.model.AuditLog;
import lu.bank.custodyconnect.model.Event;
import lu.bank.custodyconnect.model.dto.EventDto;
import lu.bank.custodyconnect.repository.AuditLogRepository;
import lu.bank.custodyconnect.repository.EventRepository;

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
