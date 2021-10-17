package com.hovo.shop.service;

import com.hovo.shop.exception.ForbiddenException;
import com.hovo.shop.exception.ResourceNotFoundException;
import com.hovo.shop.model.Category;
import com.hovo.shop.model.Product;
import com.hovo.shop.repository.CategoryRepository;
import com.hovo.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Page<Category> getCategories(Integer pageNumber, Integer size) {
        Pageable page = PageRequest.of(pageNumber, size);
        return categoryRepository.findAll(page);
    }


    public Category getCategoryById(@PathVariable(value = "id") Long categoryId)
            throws ResourceNotFoundException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id :: " + categoryId));
    }


    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }


    @Transactional
    public Category updateCategory(Long categoryId,
                                   Category categoryDetails) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id :: " + categoryId));

        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }


    @Transactional
    public void deleteCategory(Long categoryId)
            throws ResourceNotFoundException, ForbiddenException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id :: " + categoryId));
        Pageable page = PageRequest.of(0, 1);
        Page<Product> pageProd = productRepository.getProductsByCategoryId(categoryId, page);
        if(pageProd.getTotalElements() > 0 ){
            throw new ForbiddenException("Can not delete the category. Currently we have products which are attached to the category id ::"
                    + categoryId);
        }
        categoryRepository.delete(category);
    }

}
