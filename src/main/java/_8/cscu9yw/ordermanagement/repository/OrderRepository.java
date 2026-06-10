package _8.cscu9yw.ordermanagement.repository;

import _8.cscu9yw.ordermanagement.model.Order;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    //  method to get all orders for a specific customer
    // spring data jpa automatically implements this method
    List<Order> findByCustomerId(Long customerId);
}
