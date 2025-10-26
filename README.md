# -Custody-Connect-Audit-Ready-Interface-Event-Logging-Platformhttps://github.com/popescu-alexandru-unilu/-Custody-Connect-Audit-Ready-Interface-Event-Logging-Platform.git
Project Title: “Custody Connect” — Audit-Ready Interface & Event Logging Platform
🔍 Project Summary

Develop a modular Java/Jakarta EE application that captures, reconciles, and audits custody events — such as securities transactions, fund movements, or depositary updates — between internal and external systems.

The goal: create a robust, test-automated interface and event-logging solution that improves transparency, data quality, and regulatory audit readiness in banks.no affiliation and no proprietary assets used.


 Requirement	Project 
Support application owners/developers	I will extend and document real banking processes (custody & depositary) through a working prototype.
Work in agile projects with own tasks	Each component (logging, reconciliation, REST API) can be delivered iteratively.
Programming (Java EE)	Core service built in Jakarta EE with JAX-RS, JPA, and CDI.
Test automation	Full test suite with JUnit 5, REST-assured, Mockito.
Interface development	REST endpoints to send/receive custody events and status updates.
IT requirements management	Functional/technical specs documented in Confluence or Markdown, with data field mapping and flow diagrams.
🧱 System Overview

Main Components

Event Capture Service

REST API (/api/events) for ingesting custody or fund-admin messages (JSON).

Input validation, schema checks, timestamping.

Reconciliation Engine

Compares internal records vs. external data feeds (e.g., fund admin or custodian).

Flags mismatches and status changes.

Audit Trail Module

Writes immutable event logs to a database (PostgreSQL/H2).

Exposes read-only endpoint (/api/audit) for review and compliance.

Monitoring Dashboard (optional)

Simple JSF or lightweight web UI to view recent events and errors.

⚙️ Tech Stack
Layer	Technology
Backend	Java 17, Jakarta EE (JAX-RS, JPA, CDI)
Database	PostgreSQL or H2
Build/CI	Maven, GitHub Actions or Jenkins
Testing	JUnit 5, REST-assured, Mockito
Version Control	Git
Documentation	Markdown / Swagger OpenAPI
🚀 Expected Deliverables (10–12 Weeks)

Requirements analysis & use-case documentation (custody/depositary flows).

Working prototype: event capture + reconciliation + audit trail.

REST API documentation (OpenAPI / Swagger).

Automated unit/integration tests.

Final technical report and short demo.

🎓 Learning Outcomes

Practical experience in enterprise Java development in a regulated financial setting.

Understanding of custody and depositary operations (core for Luxembourg).

Skills in interface design, data validation, and reconciliation logic.

Hands-on exposure to test automation and DevOps workflows.


Project Kickoff: “Custody Connect” – Audit-Ready Interface & Event Logging Platform

Date: 10/17/2025
Location: Luxembourg – IT 
Prepared by: Popescu Alexandru Bogdan


1. Project Overview

Custody Connect is a Java/Jakarta EE–based prototype designed to strengthen transparency and auditability in custody and depositary operations.
It provides structured logging, reconciliation, and interface monitoring for securities events (e.g. trade settlements, fund movements, or account transfers) between internal systems and external custodians or fund administrators.

The project supports DekaBank Luxembourg’s role in the custody, depositary, and corporate client business — aligning with ongoing efforts to modernize IT interfaces, increase automation, and ensure compliance in the post-trade environment.

2. Objectives
#	Objective	Description
1	Improve process transparency	Introduce structured event logging and traceability for custody operations.
2	Support data reconciliation	Identify discrepancies between internal and external data sources (fund admin, custodian).
3	Enable audit readiness	Build an immutable audit trail accessible via REST APIs.
4	Strengthen IT documentation	Capture and structure functional/technical requirements for custody-related interfaces.
5	Enhance team agility	Deliver modular, testable components in an iterative (agile) approach.
3. Scope
In-Scope

Backend development using Jakarta EE (JAX-RS, JPA, CDI).

REST interfaces for custody event ingestion and retrieval.

Database persistence and audit trail functionality.

Automated testing (unit + integration).

Technical documentation (OpenAPI/Swagger + Markdown).

Out-of-Scope

Full front-end user interface (only a minimal dashboard if needed).

Real transaction connectivity with live systems (simulation only).

Production deployment (prototype scope).

4. Deliverables
Deliverable	Description	Target Date
1. Requirements Document	Analysis of custody event data flows & functional specs	Week 2
2. Service Prototype	REST-based backend with audit logging & reconciliation logic	Week 6
3. Automated Tests	JUnit + REST-assured coverage	Week 8
4. API Documentation	Swagger/OpenAPI specs	Week 9
5. Final Report & Demo	Technical summary + live demo	Week 10–12
5. Technology Stack
Layer	Technology
Programming	Java 17, Jakarta EE (JAX-RS, JPA, CDI)
Database	PostgreSQL or H2
Build / CI	Maven, Jenkins or GitHub Actions
Testing	JUnit 5, REST-assured, Mockito
Version Control	Git
Documentation	Swagger/OpenAPI, Markdown, Confluence (if available)
6. Project Organization
Role	Name / Example	Responsibility
Project Sponsor	DekaBank Luxembourg IT Management	Approval, alignment with business strategy
Project Supervisor	Application Owner (Custody / Depositary Systems)	Technical guidance, review
Project Lead	(Your Name)	Design, implementation, testing, documentation
Supporting Team	IT Developers, QA, Business Analysts	Input, peer review, test data
7. Methodology & Timeline

The project will follow an agile mini-sprint structure (2-week iterations):

Phase	Duration	Key Activities
Initiation (Weeks 1–2)	Kickoff, requirements collection, architecture design	
Development (Weeks 3–6)	Implementation of API, database, reconciliation logic	
Testing (Weeks 7–9)	Automated testing, error handling, API validation	
Documentation & Demo (Weeks 10–12)	User guide, final report, demo presentation	
8. Risks & Mitigation
Risk	Impact	Mitigation
Limited access to real custody data	Medium	Use anonymized / synthetic event data
Scope creep or unclear requirements	High	Frequent reviews and sprint feedback
Technical integration issues	Medium	Early API prototyping & mock testing
Time constraints	Medium	Prioritize core deliverables, optional features later
9. Success Criteria

Functional REST API deployed locally and demonstrable.

90%+ automated test coverage on core modules.

Documentation complete and understandable by other developers.

Positive feedback from application owners on usability and maintainability.

10. Next Steps

Confirm kickoff participants and meeting date.

Review current custody/depositary process maps with IT application owners.

Gather sample data structures or message formats (JSON/XML).

Finalize system architecture and development environment setup.

Structure

Run Locally (WildFly + Frontend)
 - Backend context root: The WAR is configured to deploy at root via `src/main/webapp/WEB-INF/jboss-web.xml`, so API routes are under `/api/v1/...`.
 - Start backend:
   - Build WAR: `mvn clean package`
   - Deploy to WildFly (e.g., copy `target/custody-connect.war` to your WildFly deployments or use the wildfly-maven-plugin).
   - Backend base should be `http://localhost:8080/api/v1`.
 - Start frontend (CRA):
   - In `frontend/`, ensure `.env.development` contains `REACT_APP_API_BASE=/api/v1`.
   - `npm install && npm start`.
   - CRA dev server proxies to `http://localhost:8080` (see `frontend/package.json`), so browser requests to `/api/v1/...` reach the backend.
 - Verify endpoints:
   - `curl -i http://localhost:8080/api/v1/health`
   - `curl -i "http://localhost:8080/api/v1/events/count"`
   - `curl -i "http://localhost:8080/api/v1/audit?max=10"`
 - Troubleshooting:
   - If you see 404 at `http://localhost:3001/api/v1/...`, the frontend is hitting the dev server without proxying. Ensure CRA is running and the proxy is set, or set `REACT_APP_API_BASE` to a full URL like `http://localhost:8080/api/v1` and restart `npm start`.
   - The UI shows a banner if the API base is unreachable; it displays the base it is using.

custody-connect/
│
├── 📄 README.md # Project introduction & setup instructions
├── 📄 PROJECT_STRUCTURE.md # (this file)
│
├── 📂 src/
│ ├── main/
│ │ ├── java/
│ │ │ └── lu/bank/custodyconnect/
│ │ │ ├── controller/ # REST controllers (JAX-RS)
│ │ │ ├── model/ # JPA entities (Event, AuditLog, etc.)
│ │ │ ├── repository/ # Data access layer (JPA repositories)
│ │ │ ├── service/ # Business logic (Reconciliation, Audit)
│ │ │ └── util/ # Utility classes (validation, mapping)
│ │ └── resources/
│ │ ├── META-INF/persistence.xml
│ │ ├── application.properties
│ │ └── logback.xml # Logging configuration
│ │
│ └── test/
│ ├── java/
│ │ └── lu/bank/custodyconnect/
│ │ ├── controller/
│ │ ├── service/
│ │ └── repository/
│ └── resources/
│ └── test-data/ # JSON/XML test files
│
├── 📂 api/
│ ├── openapi.yaml # Swagger/OpenAPI specification
│ └── postman_collection.json # Optional API test collection
│
├── 📂 docs/
│ ├── requirements.md # Functional & non-functional requirements
│ ├── architecture.md # System design, diagrams, and data flow
│ ├── testing-strategy.md # Test automation & coverage plan
│ ├── deployment-guide.md # Local setup & build instructions
│ ├── changelog.md # Iteration updates
│ └── final-report.md # Internship summary & results
│
├── 📂 scripts/
│ ├── init-db.sql # DB initialization
│ ├── seed-events.sql # Sample event data
│ └── run-local.sh # Local startup helper script
│
├── 📂 config/
│ ├── docker-compose.yml # Optional local environment setup
│ ├── Jenkinsfile # Optional CI/CD pipeline
│ └── application.yml # Configuration file example
│
├── 📂 data/
│ ├── samples/ # Sample JSON event payloads
│ └── schema/ # JSON schema definitions
│
└── 📂 diagrams/
├── architecture.png # System architecture overview
├── dataflow.png # Event flow diagram
└── timeline.png # Project plan or sprint timeline
# Health check
curl http://localhost:8080/custody-connect/api/health

# List events
curl "http://localhost:8080/custody-connect/events?page=0&size=10"

# Export events as CSV
curl "http://localhost:8080/custody-connect/events/export?max=10" -o events.csv

# View audit logs
curl "http://localhost:8080/custody-connect/audit?max=20"

http://localhost:8080/custody-connect/api/audit
http://localhost:8080/custody-connect/api/events
http://localhost:8080/custody-connect/api
1) API polish

OpenAPI + Swagger UI
Why: discoverable APIs, easier handover.
How: Add MicroProfile OpenAPI annotations; expose /openapi. Optionally bundle Swagger UI static files under /swagger.

API versioning
Why: evolve endpoints safely.
How: mount @ApplicationPath("/api/v1"); introduce /api/v2 later. Keep DTOs per version package.

Idempotency keys
Why: safe retries from upstream systems.
How: Accept header Idempotency-Key; store a request hash → response mapping table; short TTL; return cached 202/200 if seen.

Total count & cursor pagination
Why: UI needs counts; large datasets need cursors.
How: Add GET /events/count?...; implement cursor tokens (createdAt,id) to fetch next pages efficiently.

Rate limiting / quota
Why: protect from bursts.
How: Small in-memory token bucket per client in a ContainerRequestFilter; log to audit when throttled.

2) Compliance, audit & integrity

Hash-chained audit logs
Why: tamper-evident trails.
How: For each AuditLog, compute hash = SHA256(prevHash + payload) and store prevHash. Verify chain on demand.

Evidence export ZIP
Why: one-click audit evidence.
How: GET /events/{id}/evidence → ZIP with payload, audit trail, CSV/JSON diff, hash chain proof.

Field-level PII masking
Why: least privilege views.
How: Mask accountId, references by default in serializers; unmask if role AUDIT or ADMIN (Jakarta Security).

Retention & purge jobs
Why: storage + regulation.
How: EJB Timer / MP Fault Tolerance scheduled job: move old rows to archive tables, then purge; audit every run.

3) Data quality & reconciliation brains

JSON Schema validation
Why: strict contract for payload.
How: Add a validator (everit/just JSON schema) in ValidationService; store schema id + validation result in AuditLog.

Rules engine (simple)
Why: configurable recon rules without redeploy.
How: Store rules (JSON) in DB; evaluate with MVEL or easy Java predicates; log which rule failed.

Diff snapshots
Why: show exactly what changed.
How: Persist normalized key fields (ISIN, qty, dates); compute DiffUtil map; store in audit_logs.details for mismatches.

4) Reliability & integration

Outbox pattern
Why: avoid dual-write issues when notifying downstream.
How: Table outbox_events written in the same TX; background job (or Kafka/JMS producer) publishes and marks sent.

Dead-letter queue (DLQ)
Why: capture bad messages.
How: If ingest validation fails, write to dlq_events with reason and payload; have a reprocess endpoint.

JMS/Kafka adapters (later)
Why: real custody feeds are messaging-based.
How: Create a “source adapter” SPI; start with REST; add JMS consumer or Kafka consumer module later.

5) Security & access control

OIDC with Keycloak
Why: SSO + roles.
How: WildFly Elytron OIDC adapter; protect /api/*; roles: OPS, AUDIT, ADMIN. Use annotations: @RolesAllowed.

API tokens (service accounts)
Why: system-to-system integrations.
How: api_tokens table with hashed token, scopes; ContainerRequestFilter validates Authorization: Bearer ....

Request signature (HMAC)
Why: integrity from external custodians.
How: Accept X-Signature header (HMAC-SHA256 over body); verify via shared secret per counterparty.

Field-level encryption (selective)
Why: sensitive fields at rest.
How: JPA AttributeConverter using AES-GCM (keys from WildFly credential store); apply to payload or PII columns.

6) Observability & ops

Structured logging (JSON)
Why: searchable logs.
How: WildFly logging to JSON; include eventId, action, clientId. Keep human logs INFO, business events AUDIT.

Metrics & health
Why: dashboards/alerts.
How: MP Metrics counters: events received, mismatches, DLQ size; MP Health checks → /health/ready, /health/live.

Tracing (OpenTelemetry)
Why: end-to-end latency & root cause.
How: Add Otel Java agent to WildFly, export to Jaeger/Tempo; add spans in service methods.

7) Performance & scale

DB indexing & partitions
Why: faster queries as data grows.
How: Index (status,eventTimestamp), (type,eventTimestamp), (sourceSystem,eventTimestamp); consider monthly partitions by eventTimestamp in Postgres.

Batch endpoints
Why: reduce overhead.
How: POST /events/_bulk to ingest array; wrap in a single TX with chunking.

Caching hot lookups
Why: reduce DB hits for reference data.
How: In-memory cache (Caffeine) or JPA 2nd-level cache for read-mostly tables.

8) DevX & delivery

Flyway/Liquibase migrations
Why: predictable schema.
How: Add Flyway; start V1__init.sql (tables + indexes). Replace hbm2ddl.auto.

Testcontainers
Why: real Postgres in tests.
How: Integration tests spin up Postgres; verify repositories & services.

Contract tests
Why: API stability.
How: WireMock/Rest-assured tests pinned to OpenAPI examples.

Feature flags
Why: safe rollout.
How: Simple DB-backed feature_flags checked in services; toggle recon rules, export, etc.

9) Housekeeping endpoints (admin only)

POST /admin/reconcile?from=&to=&source= → backfill reconcile.

POST /admin/purge?before= → retention enforce.

GET /admin/stats → counts by type/status/day.

POST /admin/reprocess/{dlqId} → retry DLQ item.

Secure with @RolesAllowed("ADMIN") and log every call to audit.

10) Quick wins to implement first (order I’d pick)

OpenAPI + versioning

Flyway migrations (turn off hbm2ddl)

Audit hash chain + evidence export

Total count + cursor pagination

Retention job + indices

OIDC (Keycloak) RBAC

Outbox + DLQ

Metrics/Health/Tracing
