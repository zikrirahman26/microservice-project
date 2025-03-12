package microservice.productservice.com.service;

import lombok.RequiredArgsConstructor;
import microservice.productservice.com.dto.ProductRequest;
import microservice.productservice.com.dto.ProductResponse;
import microservice.productservice.com.entity.Product;
import microservice.productservice.com.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        productRepository.save(product);
        return productResponse(product);
    }

    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        productRepository.save(product);
        return productResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        productRepository.delete(product);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return productResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::productResponse).collect(Collectors.toList());
    }

    public ProductResponse productResponse(Product product) {
        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .build();
    }
}
