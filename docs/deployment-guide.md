# Deployment Guide

## Local Setup

1. Clone the repository
2. Run `./mvnw clean install`
3. Execute `./scripts/run-local.sh`

## Build Instructions

- Use Maven for building the application
- Run unit tests with `./mvnw test`
- Package with `./mvnw package`

## Environment Requirements

- Java 11 or higher
- Maven 3.6+
- H2 database (in-memory for local)

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080
