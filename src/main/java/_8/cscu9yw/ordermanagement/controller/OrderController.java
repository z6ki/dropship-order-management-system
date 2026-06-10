package _8.cscu9yw.ordermanagement.controller;

import _8.cscu9yw.ordermanagement.model.Order;
import _8.cscu9yw.ordermanagement.model.Product;
import _8.cscu9yw.ordermanagement.repository.OrderRepository;
import _8.cscu9yw.ordermanagement.repository.CustomerRepository;
import _8.cscu9yw.ordermanagement.repository.ProductRepository;
import _8.cscu9yw.ordermanagement.service.WholesalerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin // Allow cross origin requests for frontend backend communication
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final WholesalerService wholesalerService;
    private final ProductRepository productRepository;

    // Constructor for dependency injection of repositories and service
    @Autowired
    public OrderController(OrderRepository orderRepository, 
                           CustomerRepository customerRepository,
                           WholesalerService wholesalerService,
                           ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.wholesalerService = wholesalerService;
        this.productRepository = productRepository; 
    }

    // Place  order for  customer
    @PostMapping("/api/customers/{customerId}/orders")
    public ResponseEntity<Order> placeOrder(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> orderRequest) {

        // check customer exists
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found.");
        }

        // get productId and quantity from request body
        Object prodObj = orderRequest.get("productId");
        Object qtyObj = orderRequest.get("quantity");
        if (prodObj == null || qtyObj == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing productId or quantity.");
        }

        String productId = prodObj.toString();
        int quantity;
        try {
            quantity = (qtyObj instanceof Number) ? ((Number) qtyObj).intValue() : Integer.parseInt(qtyObj.toString());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity type.");
        }
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive.");
        }

        // get current wholesaler product info
        Product wholesalerProduct = wholesalerService.getProductById(productId);
        if (wholesalerProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or removed by wholesaler.");
        }
        
        // get stable retail price from the data
        Optional<Product> localProductOpt = productRepository.findById(productId);
        if (localProductOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not available for sale by retailer.");
        }
        
        BigDecimal retailUnitPrice = localProductOpt.get().getPrice(); // Use stable local retail price
        // Calculate retail price and total
        BigDecimal totalPrice = retailUnitPrice.multiply(BigDecimal.valueOf(quantity));

        // Create new Order object with auto-generated ID and pending status
        Order order = new Order(null, customerId, productId, quantity, totalPrice, Order.STATUS_PENDING); // FPassing null for auto id
        order = orderRepository.save(order); // Capturing auto generated id

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Get all past orders for a customer
    @GetMapping("/api/customers/{customerId}/orders")
    public List<Order> viewPastOrders(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    // Cancel an order if its not shipped
    @DeleteMapping("/api/customers/{customerId}/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        // Check that order exists and belongs to customer
        if (orderOpt.isEmpty() || !orderOpt.get().getCustomerId().equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found or access denied.");
        }
        // dont cancel if shipped
        Order order = orderOpt.get();
        if (Order.STATUS_SHIPPED.equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already shipped; cannot cancel.");
        }

        order.setStatus(Order.STATUS_CANCELLED);
        orderRepository.save(order);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Operator views all orders
    @GetMapping("/api/operator/orders")
    public List<Order> viewAllOrders() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    // Operator updates order status
    @PutMapping("/api/operator/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found.");
        }

        Order order = orderOpt.get();
        if (!Order.STATUS_SHIPPED.equals(newStatus) && !Order.STATUS_OUT_OF_STOCK.equals(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be 'Shipped' or 'Out of Stock'.");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // operator gets total revenue of shipped order for customer
    @GetMapping("/api/operator/customers/{customerId}/revenue")
    public ResponseEntity<BigDecimal> viewCustomerRevenue(@PathVariable Long customerId) {
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        List<Order> customerOrders = orderRepository.findByCustomerId(customerId);

        BigDecimal totalRevenue = customerOrders.stream()
                .filter(order -> Order.STATUS_SHIPPED.equals(order.getStatus()))
                .map(Order::getPriceAtOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResponseEntity<>(totalRevenue, HttpStatus.OK);
    }

    // operator gets total profit of shipped orders for  customer
    @GetMapping("/api/operator/customers/{customerId}/profit")
    public ResponseEntity<BigDecimal> viewCustomerProfit(@PathVariable Long customerId) {
        if (customerRepository.findById(customerId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found.");
        }
        List<Order> customerOrders = orderRepository.findByCustomerId(customerId);

        BigDecimal totalProfit = BigDecimal.ZERO;
        final BigDecimal FIXED_PROFIT_MARGIN = BigDecimal.valueOf(5);// Fixed margin per unit

        for (Order order : customerOrders) {
            if (!Order.STATUS_SHIPPED.equals(order.getStatus())) continue;            
            // Calculate profit based on fixed margin times quantity
            BigDecimal profitPerOrder = FIXED_PROFIT_MARGIN.multiply(BigDecimal.valueOf(order.getQuantity()));
            totalProfit = totalProfit.add(profitPerOrder);
        }
        return new ResponseEntity<>(totalProfit, HttpStatus.OK);
    }

    //Operator endpoint to update product retail price stored locally
    @PutMapping("/api/operator/products/{productId}/price")
    public ResponseEntity<Void> updateProductPrice(@PathVariable String productId, @RequestParam BigDecimal newPrice) {
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product ID not found.");
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New price must be positive.");
        }        
        Product product = productOpt.get();
        // Update the local retail price
        product.setPrice(newPrice); 
        productRepository.save(product);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // get customer order excluding those marked out of stock
    @GetMapping("/api/customers/{customerId}/orders/refresh")
    public List<Order> getAvailableOrders(@PathVariable Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .filter(order -> !Order.STATUS_OUT_OF_STOCK.equals(order.getStatus()))
                .collect(Collectors.toList());
    }
}