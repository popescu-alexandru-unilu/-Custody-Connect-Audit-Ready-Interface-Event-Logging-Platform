export interface EventModel {
  id: string;
  type: string;
  sourceSystem: string;
  status: 'pending' | 'processed' | 'failed' | string;
  eventTimestamp: string; // ISO
  payload?: string | null;
}

export interface AuditLogModel {
  id: string;
  eventId: string | null;
  action: string;
  level: 'INFO' | 'WARN' | 'ERROR' | string;
  loggedAt: string; // ISO
  message?: string | null;
  hash?: string | null;
  prevHash?: string | null;
}