package com.hovo.shop.model;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {

    public enum OnlineStatus {
        DELETED, ACTIVE, BLOCKED
    }

    private long id;
    private String name;
    private OnlineStatus onlineStatus;
    private String shortDescription;
    private String longDescription;

    private Set<Category> categories = new HashSet<>();

    public Product() {
    }

    public Product(String name, OnlineStatus onlineStatus, String shortDescription, String longDescription) {
        this.name = name;
        this.onlineStatus = onlineStatus;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    @Length(min = 3, max = 300, message = "Name length between 3 to 300")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Type(type = "com.hovo.shop.model.EnumTypePostgreSql")
    @Column(name = "online_status")
    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @Column(name = "short_description", nullable = true)
    @Length(max = 300, message = "Short description length between 0 to 300")
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Column(name = "long_description", nullable = true)
    @Length(max = 3000, message = "Long description length between 0 to 3000")
    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }


    @ManyToMany
    @JoinTable(
            name = "products_categories",
            joinColumns = {
                    @JoinColumn(name = "product_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "category_id")
            }
    )
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }


    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name
                + ", onlineStatus = " + (onlineStatus == null? "null" :onlineStatus.name())
                + ", shortDescription = " + shortDescription
                + ", longDescription=" + longDescription + "]";
    }

}
