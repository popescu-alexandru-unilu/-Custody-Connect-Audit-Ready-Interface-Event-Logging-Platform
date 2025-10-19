# System Architecture

## Overview

Custody Connect is a Java-based application using Spring Boot for the event logging and audit system.

## Components

- **Controllers**: REST API endpoints
- **Services**: Business logic for reconciliation and audit
- **Repositories**: Data access layer
- **Models**: JPA entities for Event and AuditLog

## Data Flow

1. Receive event via API
2. Log to database
3. Perform reconciliation
4. Generate audit logs

## Diagrams

(TODO: Add diagrams)
- Architecture diagram
- Data flow diagram
