package lu.bank.custodyconnect.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "dlq_events")
public class DlqEvent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String originalEventId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(nullable = false)
  private String reason;

  @Column(columnDefinition = "TEXT")
  private String errorDetails;

  private String eventType;
  private String destination;

  @Column(nullable = false)
  private Instant failedAt = Instant.now();

  @Column(nullable = false)
  private int retryCount = 0;

  @Column(nullable = false)
  private boolean reprocessable = true;

  // constructors, getters, setters
  public DlqEvent() {}

  public DlqEvent(String originalEventId, String payload, String reason,
                  String eventType, String destination) {
    this.originalEventId = originalEventId;
    this.payload = payload;
    this.reason = reason;
    this.eventType = eventType;
    this.destination = destination;
  }

  public DlqEvent(OutboxEvent failedEvent, String reason, String errorDetails) {
    this.originalEventId = failedEvent.getEventId();
    this.payload = failedEvent.getPayload();
    this.reason = reason;
    this.errorDetails = errorDetails;
    this.eventType = failedEvent.getEventType();
    this.destination = failedEvent.getDestination();
    this.retryCount = failedEvent.getRetryCount();
  }

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getOriginalEventId() { return originalEventId; }
  public void setOriginalEventId(String originalEventId) { this.originalEventId = originalEventId; }

  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public String getErrorDetails() { return errorDetails; }
  public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }

  public String getDestination() { return destination; }
  public void setDestination(String destination) { this.destination = destination; }

  public Instant getFailedAt() { return failedAt; }
  public void setFailedAt(Instant failedAt) { this.failedAt = failedAt; }

  public int getRetryCount() { return retryCount; }
  public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

  public boolean isReprocessable() { return reprocessable; }
  public void setReprocessable(boolean reprocessable) { this.reprocessable = reprocessable; }
}
