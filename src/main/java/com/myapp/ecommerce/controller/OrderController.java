package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.CreateOrderRequest;
import com.myapp.ecommerce.entity.CustomerOrder;
import com.myapp.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

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
}