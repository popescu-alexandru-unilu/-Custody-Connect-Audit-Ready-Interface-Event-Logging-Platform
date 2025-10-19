custody-connect/
│
├── 📄 README.md # Project introduction & setup instructions
├── 📄 PROJECT_STRUCTURE.md # (this file)
│
├── 📂 src/
│ ├── main/
│ │ ├── java/
│ │ │ └── lu/dekabank/custodyconnect/
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
│ │ └── lu/dekabank/custodyconnect/
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
