package microservice.productservice.com.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.productservice.com.dto.ProductRequest;
import microservice.productservice.com.dto.ProductResponse;
import microservice.productservice.com.entity.Product;
import microservice.productservice.com.repository.ProductRepository;
import microservice.productservice.com.validation.ValidationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ValidationRequest validationRequest;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {

        validationRequest.validationRequest(productRequest);

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCost(productRequest.getCost());
        productRepository.save(product);
        return productResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {

        validationRequest.validationRequest(productRequest);

        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCost(productRequest.getCost());
        productRepository.save(product);
        return productResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return productResponse(product);
    }

    @Transactional
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::productResponse).collect(Collectors.toList());
    }

    private ProductResponse productResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .cost(product.getCost())
                .build();
    }
}
