package com.myapp.ecommerce.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductCreation() {
        // Arrange & Act
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-performance laptop");
        product.setPrice(999.99);
        product.setStockQuantity(10);

        // Assert
        assertEquals(1L, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("High-performance laptop", product.getDescription());
        assertEquals(999.99, product.getPrice());
        assertEquals(10, product.getStockQuantity());
    }

    @Test
    void testProductUpdate() {
        // Arrange
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setStockQuantity(10);

        // Act
        product.setPrice(1099.99);
        product.setStockQuantity(5);

        // Assert
        assertEquals(1099.99, product.getPrice());
        assertEquals(5, product.getStockQuantity());
    }

    @Test
    void testProductEquality() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(999.99);

        Product product2 = new Product();
        product2.setId(1L);
        product2.setName("Laptop");
        product2.setPrice(999.99);

        // Assert
        assertEquals(product1.getId(), product2.getId());
        assertEquals(product1.getName(), product2.getName());
    }

    @Test
    void testProductWithZeroStock() {
        // Arrange & Act
        Product product = new Product();
        product.setStockQuantity(0);

        // Assert
        assertEquals(0, product.getStockQuantity());
    }

    @Test
    void testProductWithNullDescription() {
        // Arrange & Act
        Product product = new Product();
        product.setName("Product");
        product.setPrice(50.0);
        product.setDescription(null);

        // Assert
        assertNull(product.getDescription());
        assertEquals("Product", product.getName());
    }
}
