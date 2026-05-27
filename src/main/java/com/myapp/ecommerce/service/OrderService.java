package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.dto.OrderItemRequest;
import com.myapp.ecommerce.entity.*;
import com.myapp.ecommerce.repository.CustomerOrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

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

        double totalPrice = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check stock (validation)
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                        "Not enough stock for product: " + product.getName());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

            double itemPrice = product.getPrice();

            // save price of item immediately
            orderItem.setPrice(itemPrice);

            // Calculate total
            totalPrice += itemPrice * itemRequest.getQuantity();

            // Update stock
            product.setStockQuantity(
                    product.getStockQuantity() - itemRequest.getQuantity());

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        return customerOrderRepository.save(order);
    }
}