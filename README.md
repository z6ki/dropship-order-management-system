# Apex DropShip

Apex DropShip is a Spring Boot RESTful order management system for a drop shipping retailer. It allows customers to browse products, place orders, view order history, and cancel unshipped orders, while operators can securely manage orders, update prices, and view revenue and profit data through protected endpoints and a web interface.

## Overview

This project was developed for the CSCU9YW Web Services assignment and is built around a multi-layered architecture using Spring Boot, JPA, H2 Database, and integration with an external wholesaler stock service. The system includes both backend REST APIs and frontend web clients for customer and operator interactions.

## Features

### Customer Features
- View products on sale.
- Place orders by product ID and quantity.
- View past orders and their statuses.
- Cancel orders that have not yet been shipped.

### Operator Features
- Secure login using an API key.
- View all customer orders.
- Update order status to **Shipped** or **Out of Stock**.
- Update local retail prices for products.
- View customer revenue and profit.

### Additional Features
- Synchronizes product data from an external wholesaler stock service.
- Uses H2 in-memory database for customer, product, and order storage.
- Includes web-based customer and operator portals built with HTML, JavaScript, and Tailwind CSS.

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Maven Wrapper
- HTML, JavaScript, Tailwind CSS

## Project Structure

```text
ordermanagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── _8/cscu9yw/ordermanagement/
│   │   │       ├── DataLoader.java
│   │   │       ├── OrdermanagementApplication.java
│   │   │       ├── StartupRunner.java
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── CustomerController.java
│   │   │       │   ├── OrderController.java
│   │   │       │   └── ProductController.java
│   │   │       ├── model/
│   │   │       │   ├── Customer.java
│   │   │       │   ├── Order.java
│   │   │       │   └── Product.java
│   │   │       ├── repository/
│   │   │       │   ├── CustomerRepository.java
│   │   │       │   ├── OrderRepository.java
│   │   │       │   └── ProductRepository.java
│   │   │       └── service/
│   │   │           ├── WholesalerService.java
│   │   │           └── WholesalerSyncService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── customer_client.html
│   │           ├── operator_client.html
│   │           └── operator_login.html
│   └── test/
│       └── java/
├── target/
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## API Endpoints

### Customer Endpoints

- `GET /api/customers` — list all customers.
- `GET /api/products` — list products.
- `POST /api/customers/{customerId}/orders` — place a new order.
- `GET /api/customers/{customerId}/orders` — view customer order history.
- `DELETE /api/customers/{customerId}/orders/{orderId}` — cancel an unshipped order.
- `GET /api/customers/{customerId}/orders/refresh` — refresh visible customer orders excluding out-of-stock items.

### Operator Endpoints

- `GET /api/operator/orders` — view all orders.
- `PUT /api/operator/orders/{orderId}/status?newStatus=Shipped` — update order status.
- `PUT /api/operator/products/{productId}/price?newPrice=value` — update product price.
- `GET /api/operator/customers/{customerId}/revenue` — view shipped revenue for a customer.
- `GET /api/operator/customers/{customerId}/profit` — view profit for a customer.

## Security

Operator endpoints are protected using an API key sent in the `X-API-KEY` request header. Requests without a valid key are rejected with an unauthorized response.

## Database

The application uses an H2 in-memory database with three main entities: `Customer`, `Product`, and `Order`. JPA repositories are used to manage persistence and CRUD operations.

## Running the Project

### Prerequisites
- Java 21 installed.
- Maven, or use the included Maven Wrapper.

### Run with Maven Wrapper

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

The backend starts as a Spring Boot application and loads sample customers on startup, while product data is synchronized from the external wholesaler service.

## Accessing the Application

Once the application is running, you can use:

- Customer portal: `http://localhost:8080/customer_client.html`
- Operator login: `http://localhost:8080/operator_login.html`
- Operator portal: `http://localhost:8080/operator_client.html`
- H2 console: `http://localhost:8080/h2-console`

## Default Configuration

The application properties include:

- Application name: `ordermanagement`
- Operator API key: `3142868`
- H2 database URL: `jdbc:h2:mem:testdb`
- H2 username: `sa`
- H2 password: `123`
- Wholesaler API base URL: `https://pmaier.eu.pythonanywhere.com/wss`

## Testing

The system was tested using:

- Manual `curl` commands for endpoint verification.
- Frontend interaction testing through the customer and operator portals.
- H2 database verification to confirm correct storage of customers, products, and orders.

## Notes

- Product data is pulled from an external wholesaler service, so available items and prices may change over time.
- Operator functionality depends on the correct API key being supplied.
- The project uses an in-memory database, so data resets when the application stops.

## Author

- Student Number: 3142868
- Module: CSCU9YW Web Services Assignment
