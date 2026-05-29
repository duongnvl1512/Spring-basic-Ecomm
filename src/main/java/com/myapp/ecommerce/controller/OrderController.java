package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.dto.OrderDetail;
import com.myapp.ecommerce.entity.CustomerOrder;
import com.myapp.ecommerce.service.OrderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public CustomerOrder createOrder(
            @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<CustomerOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDetail getOrder(@PathVariable Long id) {
        return orderService.getOrderDetailById(id);
    }

    @PutMapping("/{id}/status")
    public OrderDetail updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
}