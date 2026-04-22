package org.project.mapper;

import org.project.dto.CreateProductRequest;
import org.project.dto.UpdateProductRequest;
import org.project.model.Product;
import tools.jackson.databind.ObjectMapper;

public class ProductMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static CreateProductRequest mapToCreateRequest(String json) {
        try {
            return mapper.readValue(json, CreateProductRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid product creation data");
        }
    }

    public static UpdateProductRequest mapToUpdateRequest(String json) {
        try {
            return mapper.readValue(json, UpdateProductRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid product update data");
        }
    }
    public static String mapToJSON(Product product) {
        try {
            return mapper.writeValueAsString(product);
        } catch (Exception e) {
            throw new RuntimeException("Error converting product to JSON");
        }

    }
}
