package microservice.productservice.com.controller;

import microservice.productservice.com.dto.ApiResponse;
import microservice.productservice.com.dto.ProductRequest;
import microservice.productservice.com.dto.ProductResponse;
import microservice.productservice.com.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .data(productResponse)
                .message("Product created successfully")
                .build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.updateProduct(productRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .data(productResponse)
                .message("Product updated successfully")
                .build());
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<ProductResponse>builder()
                .data(productResponse)
                .message("Product retrieved successfully")
                .build());
    }

    @GetMapping("show-all")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> productResponses = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<List<ProductResponse>>builder()
                .data(productResponses)
                .message("Product retrieved successfully")
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<String>builder()
                .data(null)
                .message("Product deleted successfully")
                .build());
    }
}
