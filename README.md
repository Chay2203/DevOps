# Image Editor

A command-line image processing utility with a production-grade CI/CD pipeline implementing DevSecOps best practices.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [CI/CD Pipeline](#cicd-pipeline)
- [Local Development](#local-development)
- [Docker Usage](#docker-usage)
- [GitHub Secrets Configuration](#github-secrets-configuration)
- [Security Controls](#security-controls)

---

## Overview

Image Editor is a Java-based CLI tool that performs various image transformations including:

- Grayscale conversion
- Brightness adjustment
- Image rotation (90° left/right)
- Horizontal and vertical flipping
- Blur effect

This project demonstrates a complete DevOps CI/CD pipeline with security scanning, quality gates, and containerization.

---

## Features

| Operation | Description |
|-----------|-------------|
| Grayscale | Converts color images to grayscale |
| Brightness | Adjusts image brightness by percentage |
| Rotate Right | Rotates image 90° clockwise |
| Rotate Left | Rotates image 90° counter-clockwise |
| Flip Horizontal | Mirrors image left-to-right |
| Flip Vertical | Mirrors image top-to-bottom |
| Blur | Applies pixelated blur effect |

---

## CI/CD Pipeline

### Pipeline Architecture

```
┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│  Checkout   │──▶│ Setup Java  │──▶│   Linting   │──▶│    SAST     │
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
                                                             │
┌─────────────┐   ┌─────────────┐   ┌─────────────┐   ┌──────▼──────┐
│ Docker Push │◀──│ Image Scan  │◀──│Docker Build │◀──│     SCA     │
└─────────────┘   └─────────────┘   └─────────────┘   └─────────────┘
                         │                                   │
                  ┌──────▼──────┐                     ┌──────▼──────┐
                  │Runtime Test │                     │ Unit Tests  │
                  └─────────────┘                     └─────────────┘
```

### Pipeline Stages

| Stage | Tool | Why It Matters |
|-------|------|----------------|
| 1. Checkout | actions/checkout@v4 | Retrieves source code for processing |
| 2. Setup Java | actions/setup-java@v4 | Provides consistent Java 17 environment |
| 3. Cache | actions/cache@v4 | Speeds up builds by reusing dependencies |
| 4. Linting | maven-checkstyle-plugin | Enforces coding standards, prevents tech debt |
| 5. SAST | github/codeql-action | Detects code-level security vulnerabilities |
| 6. SCA | OWASP dependency-check | Identifies vulnerable dependencies |
| 7. Unit Tests | JUnit 5 / maven-surefire | Validates business logic, prevents regressions |
| 8. Build | maven-jar-plugin | Creates deployable JAR artifact |
| 9. Docker Build | docker/build-push-action | Creates container image |
| 10. Image Scan | aquasecurity/trivy-action | Scans container for CVEs |
| 11. Runtime Test | docker run | Validates container functionality |
| 12. Registry Push | docker/build-push-action | Publishes trusted image to DockerHub |

### Pipeline Triggers

- **Push to main**: Triggers full pipeline including DockerHub push
- **Pull Request**: Triggers build, test, and security scans (no push)
- **Manual**: Workflow can be triggered manually via GitHub Actions UI

---

## Local Development

### Prerequisites

- Java 17 (JDK)
- Maven 3.8+
- Docker (optional, for containerization)

### Build

```bash
# Clone the repository
git clone <repository-url>
cd Image-Editor

# Build the application
mvn clean package

# Run tests
mvn test

# Run checkstyle
mvn checkstyle:check

# Run OWASP dependency check
mvn org.owasp:dependency-check-maven:check
```

### Run Locally

```bash
# Show help
java -jar target/image-editor-1.0.0.jar --help

# Interactive mode
java -jar target/image-editor-1.0.0.jar
```

---

## Docker Usage

### Build Docker Image

```bash
docker build -t image-editor .
```

### Run Container

```bash
# Show help
docker run --rm image-editor

# Interactive mode (requires mounting image files)
docker run -it --rm -v $(pwd):/data image-editor
```

### Pull from DockerHub (after CI/CD push)

```bash
docker pull <your-dockerhub-username>/image-editor:latest
```

---

## GitHub Secrets Configuration

The CI/CD pipeline requires the following secrets to be configured in your GitHub repository:

### Required Secrets

| Secret Name | Purpose |
|-------------|---------|
| `DOCKERHUB_USERNAME` | Your DockerHub username |
| `DOCKERHUB_TOKEN` | DockerHub access token |

### Setup Instructions

1. **Create DockerHub Access Token:**
   - Go to [DockerHub](https://hub.docker.com/)
   - Navigate to: Account Settings → Security → New Access Token
   - Create a token with Read/Write permissions
   - Copy the token (it won't be shown again)

2. **Add Secrets to GitHub:**
   - Go to your GitHub repository
   - Navigate to: Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Add `DOCKERHUB_USERNAME` with your DockerHub username
   - Add `DOCKERHUB_TOKEN` with the access token from step 1

**Important:** Never hardcode credentials in the codebase. Always use GitHub Secrets.

---

## Security Controls

### DevSecOps Implementation

| Control | Tool | Risk Mitigated |
|---------|------|----------------|
| **SAST** | CodeQL | SQL injection, XSS, path traversal, OWASP Top 10 |
| **SCA** | OWASP Dependency-Check | Vulnerable dependencies, supply chain attacks |
| **Container Scan** | Trivy | OS and library vulnerabilities in Docker image |
| **Non-root User** | Dockerfile USER | Container escape, privilege escalation |
| **Secrets Management** | GitHub Secrets | Credential exposure |
| **Code Quality** | Checkstyle | Maintainability issues, technical debt |

### Security Findings

Security scan results are available in:
- **GitHub Security Tab**: CodeQL and Trivy findings
- **CI Artifacts**: OWASP Dependency-Check HTML report

---

## Project Structure

```
Image-Editor/
├── .github/
│   └── workflows/
│       └── ci.yml              # CI/CD pipeline definition
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/imageeditor/
│   │   │       └── ImageEditor.java
│   │   └── resources/
│   │       └── taylor.jpg      # Sample test image
│   └── test/
│       └── java/
│           └── com/imageeditor/
│               └── ImageEditorTest.java
├── Dockerfile                  # Multi-stage container build
├── .dockerignore              # Docker build exclusions
├── checkstyle.xml             # Code style configuration
├── pom.xml                    # Maven build configuration
└── README.md                  # This file
```

---

## License

This project is for educational purposes as part of the DevOps CI/CD assessment.
