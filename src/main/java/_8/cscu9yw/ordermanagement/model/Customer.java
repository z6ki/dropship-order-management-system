package _8.cscu9yw.ordermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Represents a customer in the system.
 */
@Entity // marks this class as a jpa entity
public class Customer {

    @Id
    private Long customerId;  // customer ID
    private String name;      // customer name
    private String email;     // customer email

    // default constructor required by jpa
    public Customer() {}

    public Customer(Long customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}