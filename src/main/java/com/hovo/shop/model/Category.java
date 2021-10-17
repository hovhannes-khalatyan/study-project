package com.hovo.shop.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "category")
public class Category {

    private long id;
    private String name;
    private Category parentCategory;

    public Category() {
    }

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Length(min = 3, max = 300, message = "Name length between 3 to 300")
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name
                + ", parentCategoryId = " + (parentCategory == null ? "null": parentCategory.getId())+"]";
    }

}
