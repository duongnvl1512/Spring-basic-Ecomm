package com.myapp.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetail {

    private Long id;

    private String customerName;

    private String customerPhone;

    private BigDecimal totalPrice;

    private String status;

    private List<OrderItemDetail> items;

    @Data
    public static class OrderItemDetail {

        private Long productId;

        private String productName;

        private Integer quantity;

        private BigDecimal price;

        private BigDecimal subtotal;
    }
}