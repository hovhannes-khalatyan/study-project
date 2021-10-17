package com.hovo.shop.repository;

import com.hovo.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product p, products_categories pc WHERE p.id = pc.product_id AND "
            + " pc.category_id = ?1",
            countQuery = "SELECT count(*) FROM product p, products_categories pc WHERE p.id = pc.product_id AND "
                    + " pc.category_id = ?1",
            nativeQuery = true)
    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);
}
