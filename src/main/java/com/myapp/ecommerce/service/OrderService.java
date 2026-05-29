package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.dto.OrderDetail;
import com.myapp.ecommerce.dto.OrderItemRequest;
import com.myapp.ecommerce.entity.*;
import com.myapp.ecommerce.repository.CustomerOrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            CustomerOrderRepository customerOrderRepository,
            ProductRepository productRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.productRepository = productRepository;
    }

    public List<CustomerOrder> getAllOrders() {
        return customerOrderRepository.findAll();
    }

    public CustomerOrder createOrder(CreateOrderRequest request) {

        CustomerOrder order = new CustomerOrder();

        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check stock (validation)
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

            BigDecimal itemPrice = BigDecimal.valueOf(product.getPrice());

            // save price of item immediately (store as Double in entity)
            orderItem.setPrice(itemPrice.doubleValue());

            // Calculate total
            totalPrice = totalPrice.add(itemPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // Update stock
            product.setStockQuantity(
                    product.getStockQuantity() - itemRequest.getQuantity());

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice.doubleValue());

        return customerOrderRepository.save(order);
    }

    public OrderDetail getOrderDetailById(Long id) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderDetail(order);
    }

    private OrderDetail mapToOrderDetail(CustomerOrder order) {
        OrderDetail dto = new OrderDetail();

        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setStatus(order.getStatus().name());
        dto.setTotalPrice(
                BigDecimal.valueOf(order.getTotalPrice()));

        List<OrderDetail.OrderItemDetail> items = order.getOrderItems()
                .stream()
                .map(item -> {
                    OrderDetail.OrderItemDetail i = new OrderDetail.OrderItemDetail();

                    i.setProductId(item.getProduct().getId());
                    i.setProductName(item.getProduct().getName());
                    i.setQuantity(item.getQuantity());

                    BigDecimal price = BigDecimal.valueOf(item.getPrice());

                    i.setPrice(price);
                    i.setSubtotal(
                            price.multiply(BigDecimal.valueOf(item.getQuantity())));
                    return i;
                })
                .toList();
        dto.setItems(items);
        return dto;
    }

    public OrderDetail updateStatus(Long id, String status) {
        CustomerOrder order = customerOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus safeStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(safeStatus);
        customerOrderRepository.save(order);
        return mapToOrderDetail(order);
    }

}