package org.project.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor

@Entity
public class Product {

    public Product(String id, String name, String brand, BigDecimal price,
                   String description, String sellerID, Boolean available,
                   Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.sellerID = sellerID;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        // version intentionally left null
    }


    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "seller_id", nullable = false)
    private String sellerID;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Version
    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}