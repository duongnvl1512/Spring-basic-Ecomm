package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

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
    void testGetAllProducts_Success() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Mouse")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetAllProducts_EmptyList() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(999.99)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testCreateProduct_Success() throws Exception {
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

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(1099.99);
        updatedProduct.setStockQuantity(15);

        Product resultProduct = new Product();
        resultProduct.setId(1L);
        resultProduct.setName("Updated Laptop");
        resultProduct.setDescription("Updated description");
        resultProduct.setPrice(1099.99);
        resultProduct.setStockQuantity(15);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(resultProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1099.99)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_Multiple() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")).andExpect(status().isOk());
        mockMvc.perform(delete("/api/products/2")).andExpect(status().isOk());

        verify(productService, times(2)).deleteProduct(anyLong());
    }
}
