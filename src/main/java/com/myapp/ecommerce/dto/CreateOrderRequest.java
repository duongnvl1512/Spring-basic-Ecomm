package com.myapp.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    private String customerName;

    private String customerPhone;

    private List<OrderItemRequest> items;
}