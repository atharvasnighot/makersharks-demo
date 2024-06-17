# User Management Application
This application provides APIs for user registration, authentication, and user profile management using Spring Boot, MySQL, JPA, and JWT.

# Prerequisites
Java 21 or higher installed
Maven
MySQL database server

# Setup


## Clone the repository:

bash
```
git clone https://github.com/atharvasnighot/makersharks-demo
```

## Database Configuration:

Create a MySQL database named demo_db.

Update application.properties located in src/main/resources with your database credentials:

```
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>
```

## Build and Run the Application:

You can run the application using Maven:
```
mvn spring-boot:run
```
Alternatively, you can build the JAR file and run it:

## The application will start on http://localhost:8080.

## Endpoints:

Register User:
```
curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "username": "user",
  "password": "password"
}'
```

Login:
```
curl -X POST http://localhost:8080/api/users/login \
-H "Content-Type: application/json" \
-d '{
  "email": "user@example.com",
  "password": "password"
}'
```

Fetch Self Profile:
```
curl -X GET http://localhost:8080/api/users/fetch/self \
-H "Authorization: Bearer <JWT-Token>"
```

Fetch User Profile by Username:
```
curl -X GET 'http://localhost:8080/api/users/fetch?username=user'
```

JWT is used for authentication. The Authorization header must include Bearer <JWT-Token> for authenticated requests.

Unit Tests are written in Tests folder

## Dependencies Used:

Spring Boot,
Spring Security,
Spring Data JPA,
MySQL Connector,
Lombok,
Spring Web,
Spring Validation,
Junit 

## Notes:

Ensure MySQL server is running and accessible.
This README assumes basic familiarity with Spring Boot and REST APIs.
