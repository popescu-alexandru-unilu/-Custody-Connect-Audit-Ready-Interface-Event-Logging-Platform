-- Initialize database schema for Custody Connect

CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    source_system VARCHAR(255) NOT NULL,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'pending',
    payload TEXT  -- JSON data
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    event_id VARCHAR(36) NOT NULL,
    action VARCHAR(255) NOT NULL,
    level VARCHAR(255) NOT NULL DEFAULT 'INFO',
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    message LONGVARCHAR,
    hash LONGVARCHAR,
    prev_hash LONGVARCHAR
);

CREATE TABLE IF NOT EXISTS reconciliation_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id VARCHAR(36),
    status VARCHAR(50),
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(id)
);
