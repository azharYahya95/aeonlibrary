# Library Management API Documentation

## Introduction
This document provides detailed information about the RESTful API designed to manage a simple library system. The API allows users to perform operations related to borrowing and returning books, as well as managing borrowers and books within the library.

## Project Setup and Run Guide

### 1. Using IntelliJ IDEA
1. **Open the Maven Tool Window:**:
    - On the right-hand side of IntelliJ, you should see the “Maven” tool window. If not, go to `View` -> `Tool Windows` -> `Maven`.
2. **Run Maven Goals:**:
    - In the Maven tool window, expand your project and navigate to the “Lifecycle” section. Here you can run common Maven goals:
      - `clean`: To clean the project
      - `compile`: To compile the code
      - `install`: To install the package into the local repository
3. **Run Your Application:**:
    - Run directly from `main` class

### 2. Using Bash File

1. **Navigate to Project Directory**:
   - Open a terminal window.
   - Navigate to the root directory of your project where the `run-dev.sh` file is located.

2. **Run the Bash Script**:
   - Make sure the script has executable permissions. You can set this with the command:
     ```bash
     chmod +x run-dev.sh
     ```
   - Execute the script with:
     ```bash
     ./run-dev.sh
     ```

3. **Verify**:
   - The application will start and listen on port `8081`. You can verify it by visiting [http://localhost:8081](http://localhost:8081) in your browser.

### 3. Using Docker

1. **Build Docker Image**:
   - Ensure you have Docker installed and running.
   - Open a terminal window and navigate to the root directory of your project (where the `Dockerfile` is located).
   - Build the Docker image using:
     ```bash
     docker build -t my-spring-boot-app .
     ```

2. **Run Docker Container**:
   - Run the Docker container with:
     ```bash
     docker run -p 8081:8081 my-spring-boot-app
     ```

3. **Verify**:
   - The application will be running inside the Docker container and listening on port `8081`. You can verify it by visiting [http://localhost:8081](http://localhost:8081) in your browser.

## API Documentation

To access the API documentation, open a web browser and navigate to:

- [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

This Swagger UI page provides interactive documentation for all the available API endpoints, including request and response formats.

## Database Usage
By Default, we use H2 Database for development purpose and due to its lightweight and ease to setup. 

We can configure use of other database using Dockerfile and replace the one in application.properties file.

## Unit Testing and Integration Testing
I also provide Unit Testing and Integration Testing to test individual components and whole process in isolation.

