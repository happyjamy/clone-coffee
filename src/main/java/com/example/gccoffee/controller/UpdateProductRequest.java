package com.example.gccoffee.controller;

import com.example.gccoffee.model.Category;

public record UpdateProductRequest(String productName, Category category, Long price, String description) {
}
