package _8.cscu9yw.ordermanagement.controller;

import _8.cscu9yw.ordermanagement.model.Product;
import _8.cscu9yw.ordermanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController // marks this class as a REST controller
@RequestMapping("/api/products")// base url for product endpoints
@CrossOrigin(origins = "*")  // allows cross origin requests
public class ProductController {

    @Autowired
    private ProductRepository productRepository; // injects the productrepository for database operations

    // get all products or limited to first 10 by default
    @GetMapping
    public List<Product> getProducts(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        if (limit <= 0) {
            return productRepository.findAll();// return all products if limit is 0 or negative
        }
        Pageable pageable = PageRequest.of(0, limit);// create pageable for limiting results
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();// return the list of products
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productRepository.findById(id);// search by id
        return product.map(ResponseEntity::ok)// return found product
                      .orElseGet(() -> ResponseEntity.notFound().build());// 404 if not found
    }

    // Add new product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        if (productRepository.existsById(product.getProductId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Product already exists
        }
        Product savedProduct = productRepository.save(product);// save new product
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // update entire product by ID
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(id);// find existing product
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
        Product product = optionalProduct.get();
        // update product details
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        productRepository.save(product);// save updates
        return ResponseEntity.ok(product); // return updated product
    }

    // Update price only
    @PutMapping("/{id}/price")
    public ResponseEntity<Void> updatePrice(@PathVariable String id, @RequestParam BigDecimal price) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();// 404 if not found
        }
        Product product = optionalProduct.get();
        product.setPrice(price); // set new price
        productRepository.save(product); // save change
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 no content on success
    }

    // endpoint to delete a product by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404 if product does not exist
        }
        productRepository.deleteById(id); // delete product
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 no content
    }
}