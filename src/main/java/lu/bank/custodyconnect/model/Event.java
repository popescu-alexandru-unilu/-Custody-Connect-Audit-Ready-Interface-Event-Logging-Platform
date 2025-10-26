package lu.bank.custodyconnect.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity @Table(name = "events")
public class Event {
  @Id
  private String id = UUID.randomUUID().toString();

  @NotNull private String type;
  @NotNull private String sourceSystem;
  @NotNull private Instant eventTimestamp = Instant.now();
  @NotNull private String status = "pending";

  @Column(length = 4000)
  private String payload;

  public Event() {}

  // getters and setters
  public String getId() { return id; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getSourceSystem() { return sourceSystem; }
  public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
  public Instant getEventTimestamp() { return eventTimestamp; }
  public void setEventTimestamp(Instant eventTimestamp) { this.eventTimestamp = eventTimestamp; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }
}
