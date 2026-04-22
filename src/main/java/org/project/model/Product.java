package org.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor

public class Product {
    private String id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String description;
    private String sellerID;
    private Boolean available;
    private Date createdAt;
    private Date updatedAt;

}