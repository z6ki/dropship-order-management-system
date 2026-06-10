package _8.cscu9yw.ordermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.math.BigDecimal;

/**
 * represents an order made by a customer for a product.
 */
@Entity
@Table(name = "customer_order")
public class Order {

    // status constants for order states
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_SHIPPED = "Shipped";
    public static final String STATUS_OUT_OF_STOCK = "Out of Stock";
    public static final String STATUS_CANCELLED = "Cancelled";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;  // unique order id
    private Long customerId;  // customer who placed the order
    private String productId;  // product ordered
    private int quantity;  // quantity ordered
    private String status;  // order status
    private BigDecimal priceAtOrder;  // total price at the time of the order

    // default constructor
    public Order() {}

    public Order(Long orderId, Long customerId, String productId, int quantity, BigDecimal priceAtOrder, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.status = status;
    }

    // getters and setters
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }
    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }
}