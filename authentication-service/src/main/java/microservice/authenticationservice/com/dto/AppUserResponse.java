package microservice.authenticationservice.com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.authenticationservice.com.model.AppUserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserResponse {

    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private AppUserRole appUserRole;
    private String status;
}
