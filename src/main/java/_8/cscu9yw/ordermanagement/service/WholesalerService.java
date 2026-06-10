package _8.cscu9yw.ordermanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Value;
import _8.cscu9yw.ordermanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import _8.cscu9yw.ordermanagement.model.Product;

/**
 * service to interact with wholesaler external api,
 * and provide local product objects for your app.
 */
@Service
public class WholesalerService {

    private final RestTemplate restTemplate = new RestTemplate();

    // inject base URL for wholesaler API from application.properties
    @Value("${wholesaler.api.baseurl}")
    private String apiBaseUrl;
    
    @Autowired
    private ProductRepository productRepository;


    /**
     * fetches all product categories from the wholesaler api.
     * returns a list of category names.
     */
    @SuppressWarnings("unchecked")
    public List<String> getCategories() {
        String rootUrl = apiBaseUrl;
        Map<String, Object> rootResponse = restTemplate.getForObject(rootUrl, Map.class);
        Map<String, Object> embedded = (Map<String, Object>) rootResponse.get("_embedded");
        List<Map<String, Object>> categoriesList = (List<Map<String, Object>>) embedded.get("categories");

        List<String> categories = new ArrayList<>();
        for (Map<String, Object> categoryMap : categoriesList) {
            categories.add((String) categoryMap.get("category"));
        }
        return categories;
    }

    /**
     * fetches and returns the first ten available wholesaler products,
     * spanning all categories. discontinued/missing products are skipped!
     */
    @SuppressWarnings("unchecked")
    public List<Product> getAllProducts() {
        List<Product> result = new ArrayList<>();
        List<String> categories = getCategories();

        // loop through wholesaler categories and stop after finding 10 available products
        outerLoop:
        for (String category : categories) {
            String url = apiBaseUrl + "/category/" + category;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
            List<Map<String, Object>> products = (List<Map<String, Object>>) embedded.get("products");

            for (Map<String, Object> productSummary : products) {
                Map<String, Object> links = (Map<String, Object>) productSummary.get("_links");
                Map<String, Object> selfLink = (Map<String, Object>) links.get("self");
                String productUrl = (String) selfLink.get("href");

                // fetch product details robustly skip if missing discontinued
                try {
                    Product product = restTemplate.getForObject(productUrl, Product.class);
                    if (product != null) {
                    	productRepository.save(product); // caches latest product
                    	result.add(product);
                    }
                } catch (RestClientException ex) {
                    // just skip missing products do not fail
                    continue;
                }

                // stop after collecting the first 10  products
                if (result.size() >= 10) {
                    break outerLoop;
                }
            }
        }
        return result;
    }

    /**
     * fetches a single product from wholesaler by its id.
     * returns null if product is missing/discontinued.
     */
    public Product getProductById(String productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            return productOpt.get();
        }
        String url = apiBaseUrl + "/product/" + productId;
        try {
            Product product = restTemplate.getForObject(url, Product.class);
            if (product != null) {
                productRepository.save(product);
            }
            return product;
        } catch (RestClientException ex) {
            return null;
        }
    }

}
