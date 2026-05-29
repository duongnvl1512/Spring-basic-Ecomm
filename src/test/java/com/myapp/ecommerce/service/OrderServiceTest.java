package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.dto.OrderDetail;
import com.myapp.ecommerce.dto.OrderItemRequest;
import com.myapp.ecommerce.entity.*;
import com.myapp.ecommerce.repository.CustomerOrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Product product1;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test product
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(999.99);
        product1.setStockQuantity(10);

        // Create order request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerName("John Doe");
        createOrderRequest.setCustomerPhone("1234567890");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        createOrderRequest.setItems(Arrays.asList(itemRequest));
    }

    @Test
    void testGetAllOrders_Success() {
        // Arrange
        CustomerOrder order1 = new CustomerOrder();
        order1.setId(1L);
        order1.setCustomerName("John Doe");

        CustomerOrder order2 = new CustomerOrder();
        order2.setId(2L);
        order2.setCustomerName("Jane Doe");

        List<CustomerOrder> orders = Arrays.asList(order1, order2);
        when(customerOrderRepository.findAll()).thenReturn(orders);

        // Act
        List<CustomerOrder> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());
        assertEquals("Jane Doe", result.get(1).getCustomerName());
        verify(customerOrderRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOrders_EmptyList() {
        // Arrange
        when(customerOrderRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<CustomerOrder> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(customerOrderRepository, times(1)).findAll();
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
            CustomerOrder order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        CustomerOrder result = orderService.createOrder(createOrderRequest);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("1234567890", result.getCustomerPhone());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(1999.98, result.getTotalPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John");
        request.setCustomerPhone("123");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(999L);
        itemRequest.setQuantity(1);

        request.setItems(Arrays.asList(itemRequest));

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        // Arrange
        Product lowStockProduct = new Product();
        lowStockProduct.setId(1L);
        lowStockProduct.setName("Laptop");
        lowStockProduct.setPrice(999.99);
        lowStockProduct.setStockQuantity(1); // Only 1 in stock

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John");
        request.setCustomerPhone("123");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5); // Requesting 5

        request.setItems(Arrays.asList(itemRequest));

        when(productRepository.findById(1L)).thenReturn(Optional.of(lowStockProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });

        assertTrue(exception.getMessage().contains("Not enough stock"));
        verify(productRepository, times(1)).findById(1L);
        verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void testCreateOrder_MultipleItems() {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setPrice(29.99);
        product2.setStockQuantity(50);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("John Doe");
        request.setCustomerPhone("1234567890");

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(2);

        request.setItems(Arrays.asList(item1, item2));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
            CustomerOrder order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        CustomerOrder result = orderService.createOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getOrderItems().size());
        assertEquals(1059.97, result.getTotalPrice());
        verify(productRepository, times(2)).findById(anyLong());
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void testGetOrderDetailById_Success() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setCustomerName("John Doe");
        order.setCustomerPhone("1234567890");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(1999.98);

        OrderItem item = new OrderItem();
        item.setProduct(product1);
        item.setQuantity(2);
        item.setPrice(999.99);

        order.setOrderItems(Arrays.asList(item));

        when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderDetail result = orderService.getOrderDetailById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("PENDING", result.getStatus());
        assertEquals(1999.98, result.getTotalPrice().doubleValue());
        verify(customerOrderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderDetailById_NotFound() {
        // Arrange
        when(customerOrderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderDetailById(999L);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(customerOrderRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateStatus_Success() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setCustomerName("John Doe");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(1999.98);

        OrderItem item = new OrderItem();
        item.setProduct(product1);
        item.setQuantity(2);
        item.setPrice(999.99);

        order.setOrderItems(Arrays.asList(item));

        when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
            CustomerOrder o = invocation.getArgument(0);
            return o;
        });

        // Act
        OrderDetail result = orderService.updateStatus(1L, "APPROVED");

        // Assert
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(customerOrderRepository, times(1)).findById(1L);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void testUpdateStatus_InvalidStatus() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateStatus(1L, "INVALID");
        });

        assertTrue(exception.getMessage().contains("INVALID"));
        verify(customerOrderRepository, times(1)).findById(1L);
        verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void testUpdateStatus_OrderNotFound() {
        // Arrange
        when(customerOrderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateStatus(999L, "APPROVED");
        });

        assertEquals("Order not found", exception.getMessage());
        verify(customerOrderRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateStatus_TransitionStates() {
        // Arrange
        CustomerOrder order = new CustomerOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(1999.98);

        OrderItem item = new OrderItem();
        item.setProduct(product1);
        item.setQuantity(1);
        item.setPrice(999.99);

        order.setOrderItems(Arrays.asList(item));

        when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
            CustomerOrder o = invocation.getArgument(0);
            return o;
        });

        // Act - Test state transitions
        OrderDetail result1 = orderService.updateStatus(1L, "APPROVED");
        assertEquals("APPROVED", result1.getStatus());

        OrderDetail result2 = orderService.updateStatus(1L, "DELIVERED");
        assertEquals("DELIVERED", result2.getStatus());

        OrderDetail result3 = orderService.updateStatus(1L, "CANCELLED");
        assertEquals("CANCELLED", result3.getStatus());

        // Assert
        verify(customerOrderRepository, times(3)).save(any(CustomerOrder.class));
    }
}
