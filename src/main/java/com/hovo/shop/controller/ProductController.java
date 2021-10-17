package com.hovo.shop.controller;

import com.hovo.shop.exception.ResourceNotFoundException;
import com.hovo.shop.model.Product;
import com.hovo.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public Page<Product> getProducts(@RequestParam(value = "pageNumber") Integer pageNumber,
                                     @RequestParam(value = "size") Integer size) {
        return productService.getProducts(pageNumber, size);
    }

    @GetMapping("/products-by-category-id")
    public Page<Product> getProductsByCategoryId(@RequestParam(value = "categoryId") Long categoryId,
                                                 @RequestParam(value = "pageNumber") Integer pageNumber,
                                                 @RequestParam(value = "size") Integer size) {
        return productService.getProductsByCategoryId(categoryId, pageNumber, size);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(value = "id") Long productId)
            throws ResourceNotFoundException {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws ResourceNotFoundException {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }


    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable(value = "id") Long productId,
                                                 @Valid @RequestBody Product productDetails) throws ResourceNotFoundException {

        Product updatedProduct = productService.updateProduct(productId, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    public Map<String, Boolean> deleteProduct(@PathVariable(value = "id") Long productId)
            throws ResourceNotFoundException {
        productService.deleteProduct(productId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
