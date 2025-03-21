package microservice.authenticationservice.com.controller;

import lombok.RequiredArgsConstructor;
import microservice.authenticationservice.com.dto.ApiResponse;
import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;
import microservice.authenticationservice.com.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api-auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<TokenResponse>builder()
                        .data(tokenResponse)
                        .message("User login successful")
                .build());
    }

    @PatchMapping("/change-password/{username}")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @PathVariable String username) {
        authService.changePassword(changePasswordRequest, username);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<String>builder()
                        .data(null)
                        .message("Password changed successfully")
                .build());
    }
}
