package org.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class ProductDTO {

    private String id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String sellerID;
    private boolean available;
}
