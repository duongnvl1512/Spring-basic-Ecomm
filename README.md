# E-Commerce Application

A full-stack e-commerce application built with Spring Boot for the backend and vanilla HTML/CSS/JavaScript for the frontend. This application allows users to browse products, manage orders, and provides an admin interface for product management.

## Project Overview

This project demonstrates a complete e-commerce system with:

- **Backend**: RESTful API built with Spring Boot 3.5.14
- **Frontend**: Interactive web interface with vanilla JavaScript
- **Database**: MySQL for persistent data storage
- **Architecture**: Layered architecture with Controller → Service → Repository pattern

## Technology Stack

### Backend

- **Java 17**
- **Spring Boot 3.5.14**
  - Spring Web (REST APIs)
  - Spring Data JPA (Database access)
  - Spring Validation (Input validation)
  - Spring DevTools (Development productivity)
- **MySQL Connector J** (Database driver)
- **Lombok** (Reduce boilerplate code)
- **Maven** (Build tool)

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript (ES6+)

## Project Structure

```
ecommerce/
├── src/
│   ├── main/
│   │   ├── java/com/myapp/ecommerce/
│   │   │   ├── EcommerceApplication.java       # Main Spring Boot entry point
│   │   │   ├── controller/                      # REST API endpoints
│   │   │   │   ├── ProductController.java
│   │   │   │   └── OrderController.java
│   │   │   ├── service/                         # Business logic
│   │   │   │   ├── ProductService.java
│   │   │   │   └── OrderService.java
│   │   │   ├── repository/                      # Database access
│   │   │   │   ├── ProductRepository.java
│   │   │   │   └── CustomerOrderRepository.java
│   │   │   ├── entity/                          # JPA entities
│   │   │   │   ├── Product.java
│   │   │   │   ├── CustomerOrder.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   └── OrderStatus.java
│   │   │   ├── dto/                             # Data Transfer Objects
│   │   │   │   ├── CreateOrderRequest.java
│   │   │   │   ├── OrderItemRequest.java
│   │   │   │   └── OrderDetail.java
│   │   │   └── exception/                       # Exception handling
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties           # Configuration
│   │       ├── static/                          # Static files
│   │       └── templates/                       # Thymeleaf templates (if used)
│   └── test/
│       └── java/com/myapp/ecommerce/
│           └── EcommerceApplicationTests.java
├── frontend/
│   ├── components/                              # Reusable HTML components
│   │   ├── navbar.html
│   │   ├── sidebar.html
│   │   └── toast.html
│   ├── js/                                      # JavaScript functionality
│   │   ├── products.js
│   │   ├── orders.js
│   │   ├── admin-products.js
│   │   └── components/
│   │       ├── loadComponents.js
│   │       └── toast.js
│   ├── css/
│   │   └── style.css
│   ├── assets/                                  # Images and other assets
│   ├── products.html
│   ├── orders.html
│   └── admin-products.html
├── pom.xml                                      # Maven configuration
├── mvnw & mvnw.cmd                              # Maven wrapper scripts
└── README.md                                    # This file
```

## Features

### Product Management

- View all products
- View product details
- Create new products (admin)
- Update product information (admin)
- Delete products (admin)

### Order Management

- Create orders
- View order details
- Track order status
- Manage order items

### Admin Dashboard

- Product management interface
- Order management
- Real-time notifications (Toast notifications)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- A modern web browser

### Database Setup

1. Create a MySQL database:

```sql
CREATE DATABASE ecommerce_db;
```

2. Update database credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Installation & Running

1. **Clone/Extract the project**

```bash
cd ecommerce
```

2. **Build the project**

```bash
./mvnw clean package
```

(On Windows: `mvnw.cmd clean package`)

3. **Run the application**

```bash
./mvnw spring-boot:run
```

(On Windows: `mvnw.cmd spring-boot:run`)

4. **Access the application**

- Backend API: `http://localhost:8080`
- Frontend (if configured): Serve `frontend/` folder through a web server

## API Endpoints

### Products API

| Method | Endpoint             | Description          |
| ------ | -------------------- | -------------------- |
| GET    | `/api/products`      | Get all products     |
| GET    | `/api/products/{id}` | Get product by ID    |
| POST   | `/api/products`      | Create a new product |
| PUT    | `/api/products/{id}` | Update a product     |
| DELETE | `/api/products/{id}` | Delete a product     |

### Orders API

| Method | Endpoint           | Description        |
| ------ | ------------------ | ------------------ |
| GET    | `/api/orders`      | Get all orders     |
| GET    | `/api/orders/{id}` | Get order by ID    |
| POST   | `/api/orders`      | Create a new order |
| PUT    | `/api/orders/{id}` | Update an order    |
| DELETE | `/api/orders/{id}` | Delete an order    |

## Configuration

### application.properties

Key configurations:

```properties
spring.application.name=ecommerce              # Application name
spring.datasource.url=jdbc:mysql://...         # Database URL
spring.jpa.hibernate.ddl-auto=update           # Auto schema update
spring.jpa.show-sql=true                       # Log SQL queries
server.port=8080                               # Server port
```

## Development

### Frontend Development

- Static HTML files serve as templates
- JavaScript components loaded dynamically
- Toast notifications for user feedback
- Responsive design with CSS

### Backend Development

- Layered architecture for separation of concerns
- Exception handling with `@ControllerAdvice`
- Input validation with Jakarta validation annotations
- CORS enabled for cross-origin requests

## Testing

Run the test suite:

```bash
./mvnw test
```

## Build & Deployment

### Create an executable JAR

```bash
./mvnw clean package
```

The JAR file will be created in the `target/` directory:

```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
```

### Build Docker Image (Optional)

```bash
./mvnw clean package
./mvnw spring-boot:build-image
```

## Common Issues & Solutions

### Database Connection Issues

- Ensure MySQL is running: `mysql -u root -p`
- Verify credentials in `application.properties`
- Check if `ecommerce_db` database exists

### Port Already in Use

- Change `server.port` in `application.properties`
- Or kill the process using port 8080:
  - Linux/Mac: `lsof -i :8080` then `kill -9 <PID>`
  - Windows: `netstat -ano | findstr :8080` then `taskkill /PID <PID> /F`

### Build Failures

- Clear Maven cache: `./mvnw clean`
- Ensure Java version: `java -version`
- Update Maven: `./mvnw -U clean install`

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Payment gateway integration
- [ ] Email notifications
- [ ] Search and filtering capabilities
- [ ] Product reviews and ratings
- [ ] Inventory management
- [ ] Shipping integration
- [ ] Admin analytics dashboard

## Troubleshooting

### Lombok Not Working

- Ensure IDE has Lombok plugin installed
- Run annotation processor: Maven → Reload Projects

### Changes Not Reflecting

- Run `./mvnw clean install`
- Restart Spring Boot application
- Clear browser cache

## Support

For issues or questions, please review the code comments or create an issue in your repository.

## License

This project is provided as-is for educational and commercial use.

---

**Last Updated**: May 30, 2026  
**Version**: 0.0.1-SNAPSHOT
