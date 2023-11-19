package com.example.gccoffee.controller.api;

import com.example.gccoffee.controller.CreateProductRequest;
import com.example.gccoffee.controller.UpdateProductRequest;
import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.service.ProductService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductRestController {

  private final ProductService productService;

  public ProductRestController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/api/v1/products")
  public List<Product> productList(@RequestParam Optional<Category> category) {
    return category
      .map(productService::getProductsByCategory)
      .orElse(productService.getAllProducts());
  }

  @PostMapping("/api/v1/products")
  public Product createProduct(@RequestBody CreateProductRequest createProductRequest) {
    return productService.createProduct(
      createProductRequest.productName(),
      createProductRequest.category(),
      createProductRequest.price(),
      createProductRequest.description());
  }

  @PostMapping("/api/v1/products/{productId}")
  public Product updateProduct(@PathVariable Optional<UUID> productId, @RequestBody UpdateProductRequest updateProductRequest) {
    return productService.updateProduct(
      productId.get(),
      updateProductRequest.productName(),
      updateProductRequest.category(),
      updateProductRequest.price(),
      updateProductRequest.description());
  }
}
