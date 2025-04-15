# AnyMind Backend Payment Service

GraphQL backend for handling payments and viewing hourly sales breakdown.

## Tech Stack

- Java 17, Spring Boot 3
- GraphQL (Spring for GraphQL)
- PostgreSQL, Maven, Docker Compose

## Getting Started

1. **Clone the Repo**
   ```bash
   git clone https://github.com/sh0drun/anymind-backend.git
   cd anymind-backend
   ```

2. **Start PostgreSQL**
   ```bash
   docker-compose up -d
   ```

3. **Run the App**
   ```bash
   mvn spring-boot:run
   ```

---

## GraphQL Endpoint

Use Postman or similar to send GraphQL queries to:
```
http://localhost:8080/graphql
```

---

## Sample Requests

### Mutation: Pay
```graphql
mutation {
  pay(input: {
    customerId: "user1",
    price: 100.0,
    paymentMethod: "VISA",
    additionalItems: {
      last4: "1234"
    }
  }) {
    paymentId
    finalPrice
    points
  }
}
```

### Query: Sales
```graphql
query {
  sales(input: {
    startDateTime: "2025-04-15T00:00:00",
    endDateTime: "2025-04-15T23:59:59"
  }) {
    datetime
    sales
    points
  }
}
```

---

## Notes

- Data is seeded automatically at startup
- Tests can be run with:
  ```bash
  mvn test
  ```
- App is stateless and supports concurrent execution

---

## Contributors

- **Razik Anass** – *Backend Engineer*

---
