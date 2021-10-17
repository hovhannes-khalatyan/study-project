package com.hovo.shop.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import com.hovo.shop.exception.ForbiddenException;
import com.hovo.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hovo.shop.exception.ResourceNotFoundException;
import com.hovo.shop.model.Category;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/categories")
    public Page<Category> getCategories(@RequestParam(value = "pageNumber") Integer pageNumber,
                                        @RequestParam(value = "size") Integer size) {
        return categoryService.getCategories(pageNumber, size);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(value = "id") Long categoryId)
            throws ResourceNotFoundException {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok().body(category);
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category newCategory = categoryService.createCategory(category);
        return ResponseEntity.ok().body(newCategory);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long categoryId,
                                                   @Valid @RequestBody Category categoryDetails) throws ResourceNotFoundException {
        final Category updatedCategory = categoryService.updateCategory(categoryId, categoryDetails);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/categories/{id}")
    public Map<String, Boolean> deleteCategory(@PathVariable(value = "id") Long categoryId)
            throws ResourceNotFoundException, ForbiddenException {
        categoryService.deleteCategory(categoryId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
