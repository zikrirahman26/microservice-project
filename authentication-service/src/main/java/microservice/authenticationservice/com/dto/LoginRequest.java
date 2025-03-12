package microservice.authenticationservice.com.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    private String username;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$",
            message = "Password must have at least 8 characters, including uppercase, lowercase, number, and special character (!@#$%^&*)"
    )
    @NotNull(message = "Password must not be null")
    private String password;
}
