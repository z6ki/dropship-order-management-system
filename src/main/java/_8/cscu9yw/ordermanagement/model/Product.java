package _8.cscu9yw.ordermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * represents a product available in the system.
 */
@Entity
public class Product {
	@Id
	@JsonProperty("id")
    private String productId;  // unique identifier for the product
    private String description;  // product name or description
    private BigDecimal price;  // retail price of the product

    // Default constructor required by JPA
    public Product() {}

    public Product(String productId, String description, BigDecimal price) {
        this.productId = productId;
        this.description = description;
        this.price = price;
    }

    // getter and seters
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}