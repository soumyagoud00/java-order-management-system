# Java Order Management System

A Java Spring Boot REST API for managing products and customer orders.

## Features

- Create, view, update and delete products
- Place customer orders
- Calculate order totals automatically
- Reduce stock after order placement
- Prevent orders when stock is insufficient
- Cancel confirmed orders
- Restore stock after cancellation
- Validate incorrect product and order data
- Return meaningful error responses

## Technologies Used

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven
- JUnit 5
- Mockito
- MockMvc
- Postman
- Git and GitHub

## Automated Testing

- Tests executed: 13
- Tests passed: 13
- Failures: 0
- Errors: 0
- Skipped: 0

## Manual Testing

The project contains 22 documented manual test scenarios covering:

- Positive testing
- Negative testing
- Boundary-value testing
- Functional testing
- API testing
- Regression testing

## API Endpoints

### Product APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | View all products |
| GET | `/api/products/{id}` | View one product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Order APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/orders` | Place an order |
| GET | `/api/orders` | View all orders |
| GET | `/api/orders/{id}` | View one order |
| PATCH | `/api/orders/{id}/cancel` | Cancel an order |

## Run the Project

```bash
.\mvnw.cmd spring-boot:run