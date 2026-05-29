package com.myapp.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.dto.OrderDetail;
import com.myapp.ecommerce.dto.OrderItemRequest;
import com.myapp.ecommerce.entity.CustomerOrder;
import com.myapp.ecommerce.entity.OrderStatus;
import com.myapp.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private CustomerOrder order1;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        // Initialize test data
        order1 = new CustomerOrder();
        order1.setId(1L);
        order1.setCustomerName("John Doe");
        order1.setCustomerPhone("1234567890");
        order1.setStatus(OrderStatus.PENDING);
        order1.setTotalPrice(1099.99);
        order1.setCreatedAt(LocalDateTime.now());

        // Create order request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerName("Jane Doe");
        createOrderRequest.setCustomerPhone("9876543210");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        createOrderRequest.setItems(Arrays.asList(itemRequest));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        CustomerOrder savedOrder = new CustomerOrder();
        savedOrder.setId(1L);
        savedOrder.setCustomerName("Jane Doe");
        savedOrder.setCustomerPhone("9876543210");
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalPrice(1999.98);

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(savedOrder);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("Jane Doe")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void testGetAllOrders_Success() throws Exception {
        // Arrange
        CustomerOrder order2 = new CustomerOrder();
        order2.setId(2L);
        order2.setCustomerName("Jane Doe");
        order2.setCustomerPhone("9876543210");
        order2.setStatus(OrderStatus.APPROVED);
        order2.setTotalPrice(499.99);

        List<CustomerOrder> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerName", is("John Doe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].customerName", is("Jane Doe")));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetAllOrders_EmptyList() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrder_Success() throws Exception {
        // Arrange
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(1L);
        orderDetail.setCustomerName("John Doe");
        orderDetail.setCustomerPhone("1234567890");
        orderDetail.setStatus("PENDING");
        orderDetail.setTotalPrice(new BigDecimal("1099.99"));

        when(orderService.getOrderDetailById(1L)).thenReturn(orderDetail);

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("John Doe")))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(orderService, times(1)).getOrderDetailById(1L);
    }

    @Test
    void testUpdateStatus_Success() throws Exception {
        // Arrange
        OrderDetail updatedOrderDetail = new OrderDetail();
        updatedOrderDetail.setId(1L);
        updatedOrderDetail.setCustomerName("John Doe");
        updatedOrderDetail.setCustomerPhone("1234567890");
        updatedOrderDetail.setStatus("APPROVED");
        updatedOrderDetail.setTotalPrice(new BigDecimal("1099.99"));

        when(orderService.updateStatus(1L, "APPROVED")).thenReturn(updatedOrderDetail);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(orderService, times(1)).updateStatus(1L, "APPROVED");
    }
}
