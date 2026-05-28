package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // GET all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // GET product by id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // CREATE product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // UPDATE product
    public Product updateProduct(Long id, Product updatedProduct) {

        // if product not found, throw exception
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setStockQuantity(updatedProduct.getStockQuantity());

        return productRepository.save(product);
    }

    // DELETE product
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}