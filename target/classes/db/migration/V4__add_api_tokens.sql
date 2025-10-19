-- API tokens for system-to-system authentication

CREATE TABLE api_tokens (
    token_hash VARCHAR(64) PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL,
    scopes VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    active BOOLEAN DEFAULT TRUE
);
