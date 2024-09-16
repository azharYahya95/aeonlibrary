# Use a base image with Maven and JDK 17 installed for the build stage
FROM maven:3.9.0-openjdk-17 AS build

# Set working directory
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package

# Use a base image with JDK 17 installed to run the application
FROM openjdk:17-jre-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/h2-0.0.1-SNAPSHOT.jar /app/h2-0.0.1-SNAPSHOT.jar

# Set environment variables
ENV DATABASE_URL=jdbc:h2:mem:testdb
ENV DATABASE_DRIVER=org.h2.Driver
ENV DATABASE_USERNAME=sa
ENV DATABASE_PASSWORD=password
ENV DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

# Run the application
ENTRYPOINT ["java", "-jar", "/app/h2-0.0.1-SNAPSHOT.jar"]
