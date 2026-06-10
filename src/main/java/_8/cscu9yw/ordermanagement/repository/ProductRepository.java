package _8.cscu9yw.ordermanagement.repository;

import _8.cscu9yw.ordermanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    // spring data jpa provides default methods for product management
}
