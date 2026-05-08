package org.project.repository;

import org.project.model.Product;
import java.math.BigDecimal;
import java.util.List;

public interface IProductRepository {
    Product getProductByID(String id);
    List<Product> getAllProducts();
    List<Product> getAvailableProducts();
    List<Product> search(String keyword);
    List<Product> getProductsBySeller(String sellerID);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    void saveProduct(Product product);
    void deleteProduct(String id);
    void updateProduct(Product product);
}