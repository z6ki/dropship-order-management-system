package _8.cscu9yw.ordermanagement.controller;

import _8.cscu9yw.ordermanagement.model.*;
import _8.cscu9yw.ordermanagement.repository.CustomerRepository;
import _8.cscu9yw.ordermanagement.repository.ProductRepository;
import _8.cscu9yw.ordermanagement.service.WholesalerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final WholesalerService wholesalerService;
    private final ProductRepository productRepository;

    // Constructor for dependency injection
    @Autowired
    public CustomerController(CustomerRepository customerRepository, WholesalerService wholesalerService, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.wholesalerService = wholesalerService;
        this.productRepository = productRepository;
    }

    // Return all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return (List<Customer>) customerRepository.findAll();
    }

    // Return first 10 products from wholesaler as Product list
    @GetMapping("/products")
    public List<Product> getLimitedWholesalerProducts() {
        List<Product> allProducts = new ArrayList<>();
        productRepository.findAll().forEach(allProducts::add);

        // Take only first 10 products
        return allProducts.stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}