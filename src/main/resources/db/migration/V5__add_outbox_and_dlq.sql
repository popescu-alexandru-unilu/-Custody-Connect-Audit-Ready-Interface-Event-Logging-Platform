-- Outbox pattern for reliable messaging delivery

CREATE TABLE outbox_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id VARCHAR(36) NOT NULL,
    payload TEXT NOT NULL,
    destination VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    error_message TEXT,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE INDEX idx_outbox_status_created ON outbox_events (status, created_at);
CREATE INDEX idx_outbox_event_id ON outbox_events (event_id);

-- Dead-letter queue for failed messages
CREATE TABLE dlq_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_event_id VARCHAR(36),
    payload TEXT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    error_details TEXT,
    event_type VARCHAR(100),
    destination VARCHAR(255),
    failed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    retry_count INT DEFAULT 0,
    reprocessable BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_dlq_failed_at ON dlq_events (failed_at);
CREATE INDEX idx_dlq_reprocessable ON dlq_events (reprocessable, failed_at);

-- Note: Sample data can be inserted in test scripts if needed
