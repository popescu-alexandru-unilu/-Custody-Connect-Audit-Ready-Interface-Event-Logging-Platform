package lu.dekabank.custodyconnect.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Instant;
import java.util.Map;

import lu.dekabank.custodyconnect.repository.EventRepository;
import lu.dekabank.custodyconnect.service.AuditService;

@Path("/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class MetricsController {

  @Inject EventRepository events;
  @Inject AuditService audit;

  @GET
  public Map<String, Object> metrics() {
    long total = events.count(null, null, null, null, null, null);
    long processed = events.count("processed", null, null, null, null, null);
    long failed = events.count("failed", null, null, null, null, null);
    double successRate = total > 0 ? (processed * 100.0 / total) : 0.0;

    return Map.of(
      "timestamp", Instant.now().toString(),
      "totalEvents", total,
      "processed", processed,
      "failed", failed,
      "successRate", successRate,
      // Placeholders for now; wire real metrics when available
      "avgProcessingTimeMs", null,
      "activeConnections", 0,
      "auditCount", audit.count()
    );
  }
}