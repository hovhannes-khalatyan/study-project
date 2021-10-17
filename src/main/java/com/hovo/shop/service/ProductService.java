package com.hovo.shop.service;

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

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Page<Product> getProducts(Integer pageNumber, Integer size) {
        Pageable page = PageRequest.of(pageNumber, size);
        return productRepository.findAll(page);
    }

    @Transactional
    public Page<Product> getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer size) {
        Pageable page = PageRequest.of(pageNumber, size);
        return productRepository.getProductsByCategoryId(categoryId, page);
    }

    @Transactional
    public Product getProductById(Long productId)
            throws ResourceNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this id :: " + productId));
    }

    @Transactional
    public Product createProduct(Product product) throws ResourceNotFoundException {
        checkIfAllCategoriesExist(product.getCategories());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, Product productDetails) throws ResourceNotFoundException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this id :: " + productId));

        product.setName(productDetails.getName());
        product.setLongDescription(productDetails.getLongDescription());
        product.setShortDescription(productDetails.getShortDescription());
        product.setOnlineStatus(productDetails.getOnlineStatus());
        Set<Category> productCategories = productDetails.getCategories();
        if(productCategories != null && productCategories.size() > 0){
            checkIfAllCategoriesExist(productCategories);
        }
        product.setCategories(productDetails.getCategories());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId)
            throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this id :: " + productId));
        productRepository.delete(product);
    }

    private void checkIfAllCategoriesExist(Set<Category> productCategories)  throws ResourceNotFoundException {
        if(productCategories != null && productCategories.size() > 0){
            Set<Long> categoryIds = new HashSet<>();
            productCategories.forEach(category -> {
                if(category != null ){
                    categoryIds.add(category.getId());
                }
            });
            if(categoryIds.size() > 0){
                int existingCount = categoryRepository.getCountByCategoryIds(categoryIds);
                if(existingCount != categoryIds.size()){
                    throw new ResourceNotFoundException("Couldn't find all attached product categories");
                }
            }
        }
    }
}
