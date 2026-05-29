package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setDescription("High-performance laptop");
        product1.setPrice(999.99);
        product1.setStockQuantity(10);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setDescription("Wireless mouse");
        product2.setPrice(29.99);
        product2.setStockQuantity(50);
    }

    @Test
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // Act
        Optional<Product> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Laptop", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateProduct_Success() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("Keyboard");
        newProduct.setDescription("Mechanical keyboard");
        newProduct.setPrice(79.99);
        newProduct.setStockQuantity(20);

        Product savedProduct = new Product();
        savedProduct.setId(3L);
        savedProduct.setName("Keyboard");
        savedProduct.setDescription("Mechanical keyboard");
        savedProduct.setPrice(79.99);
        savedProduct.setStockQuantity(20);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        Product result = productService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Keyboard", result.getName());
        assertEquals(79.99, result.getPrice());
        assertEquals(20, result.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(1099.99);
        updatedProduct.setStockQuantity(15);

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");
        existingProduct.setDescription("High-performance laptop");
        existingProduct.setPrice(999.99);
        existingProduct.setStockQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(1099.99, result.getPrice());
        assertEquals(15, result.getStockQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Non-existent");
        updatedProduct.setPrice(99.99);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(999L, updatedProduct);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_PartialUpdate() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Laptop");
        existingProduct.setDescription("Original description");
        existingProduct.setPrice(999.99);
        existingProduct.setStockQuantity(10);

        Product updateData = new Product();
        updateData.setName("Updated Laptop");
        updateData.setDescription("Updated description");
        updateData.setPrice(1099.99);
        updateData.setStockQuantity(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        Product result = productService.updateProduct(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_Multiple() {
        // Arrange
        doNothing().when(productRepository).deleteById(anyLong());

        // Act
        productService.deleteProduct(1L);
        productService.deleteProduct(2L);

        // Assert
        verify(productRepository, times(2)).deleteById(anyLong());
    }
}
