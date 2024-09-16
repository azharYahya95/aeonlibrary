#!/bin/bash

# Set Environment Variables
export DATABASE_URL=jdbc:h2:mem:testdb
export DATABASE_DRIVER=org.h2.Driver
export DATABASE_USERNAME=sa
export DATABASE_PASSWORD=password
export DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# Build the Spring Boot Application using Maven
mvn clean package

# Run the Spring Boot Application
java -jar target/h2-0.0.1-SNAPSHOT.jar
