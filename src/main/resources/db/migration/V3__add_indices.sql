-- Add performance indices for better query performance

-- Event table indices
CREATE INDEX idx_event_status_timestamp ON events (status, event_timestamp);
CREATE INDEX idx_event_type_source ON events (type, source_system);
CREATE INDEX idx_event_timestamp_type ON events (event_timestamp, type);
CREATE INDEX idx_event_source_timestamp ON events (source_system, event_timestamp);

-- Audit log indices
CREATE INDEX idx_audit_event_action ON audit_logs (event_id, action, logged_at);
CREATE INDEX idx_audit_logged_at ON audit_logs (logged_at);
CREATE INDEX idx_audit_action_logged_at ON audit_logs (action, logged_at);

-- Reconciliation result indices
CREATE INDEX idx_recon_event_id ON reconciliation_result (event_id);
CREATE INDEX idx_recon_status_timestamp ON reconciliation_result (status, timestamp);

-- Note: ANALYZE is MySQL syntax, H2 doesn't need it
