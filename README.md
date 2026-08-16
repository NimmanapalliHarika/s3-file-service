AWS S3 File Management Service

A Spring Boot REST API for managing files using Amazon S3 with MySQL metadata storage.

The application supports secure file upload, download, deletion, S3 object listing with pagination, temporary access through Presigned URLs, file validation, metadata persistence, DTO-based responses, and centralized exception handling.

Tech Stack

Java 21

Spring Boot 4.0.7

Spring Web

Spring Data JPA

MySQL

Amazon S3

AWS SDK for Java 2.x

Maven

Docker

Docker Compose

JUnit

Features

Upload files to Amazon S3

Validate file size before upload

Validate supported file types

Generate UUID-based S3 object names

Store files under the products/ S3 prefix

Download files from S3

Delete files from S3

List S3 objects

Pagination using S3 continuation tokens

Generate S3 Presigned URLs

Presigned URLs with temporary expiry

Store file metadata in MySQL

Retrieve all stored file metadata

Retrieve metadata by database ID

Delete metadata and the corresponding S3 object

DTO-based API responses

Custom exceptions

Global exception handling

Docker support

Docker Compose support for MySQL

Spring Boot context testing

Architecture

                Client / Postman
                       |
                       v
              Spring Boot REST API
                       |
                 FileController
                       |
                       v
                  S3Service
                  /       \
                 /         \
                v           v
          Amazon S3       MySQL
          File/Object     Metadata

File Upload Flow

Client
  |
  | Multipart File
  v
FileController
  |
  | Validate size/type
  v
S3Service
  |
  | Generate UUID filename
  v
Amazon S3
  |
  | Store object
  v
MySQL
  |
  | Store file metadata
  v
Response

File Validation

The upload API validates files before storing them.

Supported File Types

JPG / JPEG

PNG

Maximum File Size

5 MB

Invalid files are rejected using the application's custom exception handling.

S3 Object Naming

Uploaded files are stored using a UUID-based object name to avoid collisions.

Objects are stored under:

products/

Example:

products/550e8400-e29b-41d4-a716-446655440000.jpg

Presigned URLs

The application generates Amazon S3 Presigned URLs for temporary access to stored files.

The Presigned URL is valid for 10 minutes.

Client
  |
  | Request Presigned URL
  v
Spring Boot
  |
  | Generate temporary URL
  v
Amazon S3
  |
  | Presigned URL
  v
Client
  |
  | Direct access
  v
S3 Object

This allows temporary access to an S3 object without exposing AWS credentials to the client.

S3 Pagination

The application supports pagination while listing S3 objects using the AWS S3 ListObjectsV2 API.

The continuation token returned by S3 is used to request the next page.

Page 1
  |
  | continuation token
  v
Page 2
  |
  | continuation token
  v
Page 3

MySQL File Metadata

File metadata is stored in MySQL after a successful S3 upload.

The database is used to maintain information about uploaded files and their corresponding S3 objects.

The application supports:

Get all file metadata

Get metadata by ID

Delete metadata by ID

Delete the associated S3 object

REST APIs

Upload File

POST /files/upload

Uploads a validated file to S3 and stores its metadata in MySQL.

Download File

GET /files/download

Downloads the requested S3 file.

List S3 Files

GET /files/s3

Lists files stored in S3 with pagination support.

Generate Presigned URL

GET /files/presigned-url

Generates a temporary Presigned URL for an S3 object.

Get All File Metadata

GET /files

Returns stored file metadata from MySQL.

Get File Metadata by ID

GET /files/{id}

Returns metadata for a specific file.

Delete File

DELETE /files/{id}

Deletes the file metadata from MySQL and the corresponding object from S3.

Check the controller implementation for the exact request parameters required by each endpoint.

Exception Handling

The application uses custom exceptions and centralized exception handling.

Implemented exception handling includes:

Invalid file

File not found

Invalid request

S3-related failures

General application errors

A global exception handler provides consistent API error responses.

DTOs

DTOs are used to separate API responses from the persistence and service layers.

This provides:

Cleaner API responses

Separation of concerns

Controlled data exposure

Better maintainability

Project Structure

s3-file-service/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── s3/
│   │   │           └── service/
│   │   │               ├── controller/
│   │   │               │   └── FileController.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── S3Service.java
│   │   │               │   └── impl/
│   │   │               │       └── S3ServiceImpl.java
│   │   │               │
│   │   │               ├── entity/
│   │   │               ├── repository/
│   │   │               ├── dto/
│   │   │               ├── exception/
│   │   │               └── config/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md

Prerequisites

Install:

Java 21

Maven

Docker

Docker Compose

MySQL or Docker

AWS account

Amazon S3 bucket

IAM credentials with required S3 permissions

Postman

Configuration

Configure AWS and database settings through environment variables or local configuration.

Example:

aws.region=${AWS_REGION}
aws.access-key=${AWS_ACCESS_KEY_ID}
aws.secret-key=${AWS_SECRET_ACCESS_KEY}
aws.s3.bucket-name=${AWS_S3_BUCKET_NAME}

Configure the MySQL connection according to your environment.

Never commit real AWS credentials or database passwords to GitHub.

Run Locally

Clone the repository:

git clone <YOUR_GITHUB_REPOSITORY_URL>

Navigate to the project:

cd s3-file-service

Build:

mvn clean install

Run:

mvn spring-boot:run

The application runs on:

http://localhost:8080

Run with Docker Compose

Start the required containers:

docker compose up --build

Stop the containers:

docker compose down

Testing

Run the test suite:

mvn test

The project includes Spring Boot application-context testing and service/API testing as implemented in the project.

AWS IAM Permissions

The application requires appropriate S3 permissions for the operations it performs.

Typical permissions include:

s3:PutObject
s3:GetObject
s3:DeleteObject
s3:ListBucket

Use the principle of least privilege and grant only the permissions required by the application.

Security

Do not commit:

AWS Access Key
AWS Secret Key
Database Password
.env
Local configuration containing secrets

Use environment variables or an AWS-supported credential provider for sensitive configuration.

Docker Architecture

                Docker Compose
                     |
          +----------+----------+
          |                     |
          v                     v
 Spring Boot Application      MySQL
          |
          |
          v
      Amazon S3

Testing Flow

1. Upload File
       |
       v
2. Validate File
       |
       v
3. Store File in S3
       |
       v
4. Store Metadata in MySQL
       |
       v
5. List Files
       |
       v
6. Generate Presigned URL
       |
       v
7. Download / Access File
       |
       v
8. Delete File
       |
       v
9. Remove Metadata

Key Concepts Demonstrated

Spring Boot

REST API development

Dependency injection

Service layer

Controller layer

DTOs

Exception handling

Validation

AWS

Amazon S3

AWS SDK for Java

S3 object operations

ListObjectsV2

Continuation-token pagination

Presigned URLs

IAM permissions

Database

MySQL

Spring Data JPA

File metadata persistence

Repository-based data access

DevOps

Docker

Docker Compose

Maven

Testing

JUnit

Spring Boot testing

API testing with Postman

Project Highlights

Java 21
     +
Spring Boot 4.0.7
     +
REST APIs
     +
Amazon S3
     +
Presigned URLs
     +
5 MB File Validation
     +
JPG / PNG Validation
     +
UUID-Based S3 Object Names
     +
S3 Pagination
     +
MySQL Metadata
     +
DTOs
     +
Global Exception Handling
     +
Docker
     +
Docker Compose
     +
JUnit
