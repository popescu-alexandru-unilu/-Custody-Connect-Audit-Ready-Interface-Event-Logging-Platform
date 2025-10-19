package lu.dekabank.custodyconnect.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "outbox_events")
public class OutboxEvent {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String eventId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(nullable = false)
  private String destination;

  @Column(nullable = false)
  private String eventType;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  private Instant processedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.PENDING;

  @Column(nullable = false)
  private int retryCount = 0;

  @Column(nullable = false)
  private int maxRetries = 3;

  @Column(columnDefinition = "TEXT")
  private String errorMessage;

  public enum Status {
    PENDING, PROCESSING, COMPLETED, FAILED
  }

  // constructors, getters, setters
  public OutboxEvent() {}

  public OutboxEvent(String eventId, String payload, String destination, String eventType) {
    this.eventId = eventId;
    this.payload = payload;
    this.destination = destination;
    this.eventType = eventType;
  }

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEventId() { return eventId; }
  public void setEventId(String eventId) { this.eventId = eventId; }

  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }

  public String getDestination() { return destination; }
  public void setDestination(String destination) { this.destination = destination; }

  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getProcessedAt() { return processedAt; }
  public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public int getRetryCount() { return retryCount; }
  public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

  public int getMaxRetries() { return maxRetries; }
  public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

  public String getErrorMessage() { return errorMessage; }
  public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

  public boolean shouldRetry() {
    return retryCount < maxRetries && status != Status.COMPLETED;
  }
}
