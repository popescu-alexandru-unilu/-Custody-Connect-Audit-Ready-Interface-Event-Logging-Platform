package lu.dekabank.custodyconnect.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity @Table(name="audit_logs", indexes = {
    @Index(name="idx_audit_event", columnList="eventId,loggedAt")
})
public class AuditLog {
  @Id private String id = UUID.randomUUID().toString();
  @Column(nullable=false) private String eventId;
  @Column(nullable=false) private String action;   // RECEIVED, RECON_MATCH, RECON_MISMATCH, ERROR
  @Column(nullable=false) private String level="INFO"; // INFO|WARN|ERROR
  @Column(nullable=false) private Instant loggedAt = Instant.now();
  @Column(length=2000) private String message;
  // Hash chain for tamper-evident audit trail
  @Column(length=64) private String hash;         // SHA256 of (prevHash + payload)
  @Column(length=64) private String prevHash;     // Previous audit log's hash

  public AuditLog() {}
  public AuditLog(String eventId, String action, String level, String message){
    this.eventId=eventId; this.action=action; this.level=level; this.message=message;
  }

  // Getters and setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getEventId() { return eventId; }
  public void setEventId(String eventId) { this.eventId = eventId; }

  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }

  public String getLevel() { return level; }
  public void setLevel(String level) { this.level = level; }

  public Instant getLoggedAt() { return loggedAt; }
  public void setLoggedAt(Instant loggedAt) { this.loggedAt = loggedAt; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public String getHash() { return hash; }
  public void setHash(String hash) { this.hash = hash; }

  public String getPrevHash() { return prevHash; }
  public void setPrevHash(String prevHash) { this.prevHash = prevHash; }
}
