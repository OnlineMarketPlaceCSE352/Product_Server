package org.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductRequest {
    private String name;
    private String brand;
    private BigDecimal price;
    private String description;
    private Boolean available;
}
