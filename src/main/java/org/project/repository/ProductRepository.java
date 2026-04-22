package org.project.repository;

import org.project.model.Product;

import java.util.ArrayList;
import java.util.List;

    public class ProductRepository {

        private static final class ProductRepositoryHolder {
            private static final ProductRepository instance = new ProductRepository();
        }

        public static ProductRepository getInstance() {
            return ProductRepositoryHolder.instance;
        }

        private final List<Product> products = new ArrayList<>();
        public List<Product> getAllProducts() {
            return null;
        }

        public Product getProductByID(String id) {
            return  null;
        }

        public List<Product> search(String keyword) {
            return null;
        }

        public List<Product> getProductsBySeller(String sellerID) {
            return null;
        }

        public void saveProduct(Product product) {
        }

        public void deleteProduct(String id) {
        }

        public void updateProduct( Product updated) {
        }

    }
