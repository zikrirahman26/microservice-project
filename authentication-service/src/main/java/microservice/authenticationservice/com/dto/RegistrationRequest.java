package microservice.authenticationservice.com.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.authenticationservice.com.model.AppUserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    private String username;

    @Email(message = "Email must be a valid format")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,}$",
            message = "Password must have at least 8 characters, including uppercase, lowercase, number, and special character (!@#$%^&*)"
    )
    @NotNull(message = "Password must not be null")
    private String password;

    @NotNull(message = "Full name must not be null")
    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @Pattern(
            regexp = "^\\d{10,15}$",
            message = "Phone number must be between 10 to 15 digits and contain only numbers")
    @NotNull(message = "Phone number must not be null")
    private String phoneNumber;

    @NotNull(message = "Role must have 'USER', 'ADMIN', 'SELLER'")
    private AppUserRole appUserRole;
}
