package _8.cscu9yw.ordermanagement;

import _8.cscu9yw.ordermanagement.model.Customer;
import _8.cscu9yw.ordermanagement.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    // Constructor for dependency injection
    public DataLoader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // This method runs immediately after the App starts
    @Override
    public void run(String... args) throws Exception {
        customerRepository.save(new Customer(1001L, "Ironman", "ironman@gmail.com"));
        customerRepository.save(new Customer(1002L, "Hulk", "hulk@gmail.com"));
        customerRepository.save(new Customer(1003L, "Spiderman", "spiderman@gmail.com"));
        customerRepository.save(new Customer(1004L, "Thor", "thor@gmail.com"));
        customerRepository.save(new Customer(1005L, "Captain America", "captain@gmail.com"));
    }
}