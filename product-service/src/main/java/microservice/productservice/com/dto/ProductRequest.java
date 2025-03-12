package microservice.productservice.com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Name product must not be blank.")
    private String name;
    private String description;

    @PositiveOrZero
    private Double price;

    @PositiveOrZero
    private Double cost;
}
