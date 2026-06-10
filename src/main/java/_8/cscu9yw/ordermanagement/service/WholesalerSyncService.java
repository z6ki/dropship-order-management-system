package _8.cscu9yw.ordermanagement.service;

import _8.cscu9yw.ordermanagement.model.Product;
import _8.cscu9yw.ordermanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class WholesalerSyncService {

    private static final String WHOLESALER_API_BASE = "https://pmaier.eu.pythonanywhere.com/wss";

    @Autowired
    private ProductRepository productRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public void synchronizeProducts() {
        Map<?, ?> rootResponse = restTemplate.getForObject(WHOLESALER_API_BASE, Map.class);
        if (rootResponse == null || !rootResponse.containsKey("_embedded")) return;

        Map<?, ?> embedded = (Map<?, ?>) rootResponse.get("_embedded");
        List<Map<String, Object>> categories = (List<Map<String, Object>>) embedded.get("categories");
        // iterate over categories to fetch products per category
        for (Map<String, Object> category : categories) {
            try {
                Map<String, Object> links = (Map<String, Object>) category.get("_links");
                Map<String, Object> self = (Map<String, Object>) links.get("self");
                String categoryHref = (String) self.get("href");

                // Fetch products per category
                Map<?, ?> categoryResponse = restTemplate.getForObject(categoryHref, Map.class);
                if (categoryResponse == null || !categoryResponse.containsKey("_embedded")) continue;

                Map<?, ?> productsEmbedded = (Map<?, ?>) categoryResponse.get("_embedded");
                List<Map<String, Object>> products = (List<Map<String, Object>>) productsEmbedded.get("products");

                for (Map<String, Object> productSummary : products) {
                    try {
                        Map<String, Object> productSummaryLinks = (Map<String, Object>) productSummary.get("_links");
                        Map<String, Object> selfLink = (Map<String, Object>) productSummaryLinks.get("self");
                        String productHref = (String) selfLink.get("href");
                        // fetch full product details
                        Map<String, Object> productDetail = restTemplate.getForObject(productHref, Map.class);
                        if (productDetail == null) continue;

                        String productId = (String) productDetail.get("id");
                        String description = (String) productDetail.get("description");
                        Double priceDouble = (Double) productDetail.get("price");
                        BigDecimal price = priceDouble == null ? BigDecimal.ZERO : BigDecimal.valueOf(priceDouble);
                        // update or create product in local repository
                        Product product = productRepository.findById(productId).orElse(new Product());
                        product.setProductId(productId);
                        product.setDescription(description);
                        product.setPrice(price);
                        productRepository.save(product);
                    } catch (HttpClientErrorException.NotFound e) {
                    	// skip missing products
                        System.out.println("Product not found, skipping: " + productSummary);
                    } catch (Exception e) {
                        System.err.println("Error fetching product details: " + e.getMessage());
                    }
                }
            } catch (HttpClientErrorException.NotFound e) {
            	// skip missing categories
                System.out.println("Category not found, skipping: " + category);
            } catch (Exception e) {
                System.err.println("Error fetching category products: " + e.getMessage());
            }
        }
    }
}
