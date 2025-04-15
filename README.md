
## Project Overview

CIS376 Assinment 4 , configured with:
- Java 11
- Maven build system
- Continuous Integration via GitHub Actions/TravisCI

## CI/CD Setup

This repository is configured with automatic build and testing through:

### GitHub Actions
- Workflow file: `.github/workflows/maven.yml`
- Triggers on every push
- Runs `mvn clean test` on Ubuntu with Java 11

### TravisCI (alternative)
- Configuration file: `.travis.yml`
- Uses OpenJDK 11
- Executes `mvn clean test`

## Build Status

The badge at the top shows the current build status:
-  Green: All tests passing
-  Red: Build/test failures