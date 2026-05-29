package com.myapp.ecommerce.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerOrderTest {

    @Test
    void testOrderCreation() {
        // Arrange & Act
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setCustomerName("John Doe");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(999.99);

        // Assert
        assertEquals(1L, order.getId());
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("1234567890", order.getCustomerPhone());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(999.99, order.getTotalPrice());
    }

    @Test
    void testOrderWithItems() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setCustomerName("John Doe");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(999.99);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(999.99);

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        // Act
        order.setOrderItems(items);

        // Assert
        assertEquals(1, order.getOrderItems().size());
        assertEquals("Laptop", order.getOrderItems().get(0).getProduct().getName());
        assertEquals(2, order.getOrderItems().get(0).getQuantity());
    }

    @Test
    void testOrderStatusTransition() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setStatus(OrderStatus.PENDING);

        // Act
        order.setStatus(OrderStatus.APPROVED);

        // Assert
        assertEquals(OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void testOrderWithTimestamp() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);

        // Assert
        assertNotNull(order.getCreatedAt());
        assertEquals(now, order.getCreatedAt());
    }

    @Test
    void testOrderTotalPrice() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setTotalPrice(100.0);

        // Act
        order.setTotalPrice(150.0);

        // Assert
        assertEquals(150.0, order.getTotalPrice());
    }
}
