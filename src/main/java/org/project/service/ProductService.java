
package org.project.service;

import org.project.dto.CreateProductRequest;
import org.project.dto.UpdateProductRequest;
import org.project.mapper.ProductMapper;
import org.project.model.Product;
import org.project.repository.ProductRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductService {

    private final ProductRepository productRepository = ProductRepository.getInstance();

    public String getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        if (products == null) return "[]";
        return getJSONArray(products);
    }

    public String getProductByID(String productID) {
        Product product = productRepository.getProductByID(productID);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        return ProductMapper.mapToJSON(product);
    }

    public void createProduct(String productJSON,String sellerID) {
        CreateProductRequest request = ProductMapper.mapToCreateRequest(productJSON);
        Date now = new Date();

        if (request.getPrice() == null || request.getPrice().doubleValue() <= 0) {
            throw new RuntimeException("Invalid price");
        }

        Product product = new Product(
                UUID.randomUUID().toString(),
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getDescription(),
                sellerID,
                true,
                now,
                now

        );

        productRepository.saveProduct(product);
    }

    public void updateProduct(String id, String productJSON) {
        Product existing = productRepository.getProductByID(id);

        if (existing == null) {
            throw new RuntimeException("Product not found");
        }

        //  lock product (no one can buy it)
        existing.setAvailable(false);
        productRepository.updateProduct(existing);

        UpdateProductRequest request = ProductMapper.mapToUpdateRequest(productJSON);

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getBrand() != null) existing.setBrand(request.getBrand());
        if (request.getPrice() != null) existing.setPrice(request.getPrice());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        existing.setUpdatedAt(new Date());

        //unlock after update
        existing.setAvailable(true);

        productRepository.updateProduct(existing);
    }


    public void deleteProduct(String id) {
        productRepository.deleteProduct(id);
    }

    public String searchProducts(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return "[]";
        }
        List<Product> products = productRepository.search(keyword);
        return getJSONArray(products);
    }

    public void markAsSold(String productID) {
        Product product = productRepository.getProductByID(productID);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        if (!product.getAvailable()) {
            throw new RuntimeException("Product already sold or unavailable");
        }

        product.setAvailable(false);

        productRepository.updateProduct(product);
    }

    public String getAvailableProductsBySeller(String sellerID) {
        List<Product> products = productRepository.getProductsBySeller(sellerID);
        return getJSONArray(products);
    }

    private String getJSONArray(List<Product> products) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("[");

        for (int i = 0; i < products.size(); i++) {
            responseBuilder.append(ProductMapper.mapToJSON(products.get(i)));
            if (i < products.size() - 1) {
                responseBuilder.append(",");
            }
        }

        responseBuilder.append("]");
        return responseBuilder.toString();
    }
}