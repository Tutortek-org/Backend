# Tutortek Backend
## Requirements
* MySQL 8.0.23
* Spring Boot 2.5.4

## Setup
In order for this to work on your local environment create a file in `src/main/resources` directory named
`application-local.properties` and compile with `--spring.profiles.active=local`. The regular `application.properties`
is used for staging environments.

## Database population
If you want your database to be repopulated compile with `--reseed` flag.

## Testing
Tests should use a separate configuration file and `JUnit 5`. All needed dependencies are already in the `pom.xml` file.
