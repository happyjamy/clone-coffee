package com.example.gccoffee.service;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultProductService implements ProductService {

  private final ProductRepository productRepository;

  public DefaultProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public List<Product> getProductsByCategory(Category category) {
    return productRepository.findByCategory(category);
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public Product createProduct(String productName, Category category, long price) {
    var product = new Product(UUID.randomUUID(), productName, category, price);
    return productRepository.insert(product);
  }

  @Override
  public Product createProduct(String productName, Category category, long price, String description) {
    var product = new Product(UUID.randomUUID(), productName, category, price, description, LocalDateTime.now(), LocalDateTime.now());
    return productRepository.insert(product);
  }

  @Override
  public Product updateProduct(UUID productId, String productName, Category category, Long price, String description) {
    var product = productRepository.findById(productId).orElseThrow();
    if(productName != null) product.setProductName(productName);
    if(category != null) product.setCategory(category);
    if(price != null) product.setPrice(price);
    if(description != null) product.setDescription(description);
    return productRepository.update(product);
  }
}