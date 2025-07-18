# CV Processing Portal - Backend (Spring Boot)

## Deployment Information

This Spring Boot backend application is deployed on an AWS EC2 instance and integrated with AWS S3 and PostgreSQL RDS.
  
- **Frontend URL:** `https://main.d6thtuvq60hvo.amplifyapp.com`  
- **Test Login Credentials:**  
  - **Username:** recruiter  
  - **Password:** recruiter123

---

## Project Overview

This is the backend module of the CV Processing Portal. It provides RESTful APIs for:

- User authentication
- Resume upload
- Candidate management
- Role-based access control

Built with Spring Boot and integrated with AWS services for cloud storage and database.

---

## Key Features

- JWT-based login authentication  
- Role-based access control   
- Secure file upload to AWS S3  
- Resume file access using pre-signed S3 URLs  
- PostgreSQL for data storage  
- JPA and Hibernate for ORM  
- Audit logging and API protection  

---

## Technology Stack

- Java 17  
- Spring Boot  
- Spring Security  
- PostgreSQL  
- AWS S3  
- JPA and Hibernate  
- Maven  
- Lombok  
- REST APIs  

---

## Prerequisites

To run the project locally, you should have the following installed:

- Java 17  
- Maven 3.8 or later  
- PostgreSQL (if running locally instead of AWS RDS)  
- An AWS account with S3 access (if testing S3 features)  

---

## How to Build and Run Locally

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/cv-portal-backend.git
cd cv-portal-backend
```

### Step 2: Configure `application.yml`

Edit the file at `src/main/resources/application.yml` and add the following values:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<your-db-host>:5432/cvdb
    username: <your-db-username>
    password: <your-db-password>

aws:
  s3:
    bucketName: <your-bucket-name>
    region: <your-region>
    accessKey: <your-access-key>
    secretKey: <your-secret-key>
```

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

By default, the backend will be available at:  
`http://localhost:8080`

---

## Authentication and Authorization

- **Login API:** `/api/auth/login`  
- Use the credentials to obtain a JWT token.  
- Include the token in the Authorization header for accessing protected APIs.

**Header Format:**

```http
Authorization: Bearer <your-token>
```

---

## Resume Upload and Download

- **Upload Endpoint:** `/api/resumes/upload`  
- Resume files are securely stored in AWS S3.  
- A pre-signed URL is generated for viewing/downloading the resume securely.

---

## Database Tables

The application uses the following main tables:

- users  
- candidates  
- cv_files  
- skills  
- jobs  
- audit_logs  

These are mapped using JPA annotations and follow relational DB principles.

---

## Common Issues and Troubleshooting

- **403 Forbidden:** JWT token is missing or invalid. Ensure you're passing the correct token in the Authorization header.  
- **AWS region error:** Check the region in `application.yml`.  
- **CORS error:** If testing from frontend, verify proper CORS configuration in the backend.  

---

## Useful Maven Commands

```bash
mvn clean               # Clean the target directory
mvn install             # Build and install dependencies
mvn spring-boot:run     # Start the application
```
