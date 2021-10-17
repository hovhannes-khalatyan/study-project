package com.hovo.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hovo.shop.model.Category;

import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT count(*) FROM category c WHERE c.id in (:categoryIds)", nativeQuery=true)
    int getCountByCategoryIds(Set<Long> categoryIds);
}
