package org.project.repository;

import org.hibernate.Session;
import org.project.model.Product;
import org.project.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class ProductRepository extends BaseRepository<Product> implements IProductRepository {

    private static final class ProductRepositoryHolder {
        private static final ProductRepository instance = new ProductRepository();
    }

    public static ProductRepository getInstance() {
        return ProductRepositoryHolder.instance;
    }

    private ProductRepository() {
        super(Product.class);
    }

    @Override
    public Product getProductByID(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return query(p -> true);
    }

    @Override
    public List<Product> getAvailableProducts() {
        return query(Product::getAvailable);
    }

    @Override
    public List<Product> search(String keyword) {
        return query(p -> matchesKeyword(p, keyword));
    }

    @Override
    public List<Product> getProductsBySeller(String sellerID) {
        return query(p -> p.getSellerID().equals(sellerID));
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return query(p -> brand.equals(p.getBrand()));
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return query(p -> p.getPrice().compareTo(minPrice) >= 0
                && p.getPrice().compareTo(maxPrice) <= 0);
    }

    private boolean matchesKeyword(Product p, String keyword) {
        String lower = keyword.toLowerCase();
        return p.getAvailable()
                && (p.getName().toLowerCase().contains(lower)
                || (p.getBrand() != null && p.getBrand().toLowerCase().contains(lower)));
    }

    @Override
    public void saveProduct(Product product) {
        save(product);
    }

    @Override
    public void deleteProduct(String id) {
        delete(id);
    }

    @Override
    public void updateProduct(Product product) {
        update(product);
    }
}