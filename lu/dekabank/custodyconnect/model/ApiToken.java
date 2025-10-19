package lu.dekabank.custodyconnect.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="api_tokens")
public class ApiToken {
  @Id private String tokenHash;
  @Column(nullable=false) private String clientId;
  @Column(nullable=false) private String scopes; // comma-separated
  @Column(nullable=false) private Instant createdAt = Instant.now();
  @Column(nullable=false) private Instant expiresAt;
  private boolean active = true;

  public ApiToken() {}

  public ApiToken(String tokenHash, String clientId, String scopes, Instant expiresAt) {
    this.tokenHash = tokenHash;
    this.clientId = clientId;
    this.scopes = scopes;
    this.expiresAt = expiresAt;
  }

  // getters/setters
  public String getTokenHash() { return tokenHash; }
  public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

  public String getClientId() { return clientId; }
  public void setClientId(String clientId) { this.clientId = clientId; }

  public String getScopes() { return scopes; }
  public void setScopes(String scopes) { this.scopes = scopes; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public boolean isExpired() { return Instant.now().isAfter(expiresAt); }

  public boolean hasScope(String scope) { return scopes.contains(scope); }
}
