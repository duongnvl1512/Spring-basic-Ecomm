package com.myapp.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

//libraries for validation
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Positive(message = "Stock quantity must be greater than 0")
    private Integer stockQuantity;

    private String imageUrl;
}
