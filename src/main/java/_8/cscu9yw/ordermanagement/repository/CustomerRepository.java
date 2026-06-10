package _8.cscu9yw.ordermanagement.repository;

import _8.cscu9yw.ordermanagement.model.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	// spring automatically generates methods for customer management
}