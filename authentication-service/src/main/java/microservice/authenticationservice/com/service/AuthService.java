package microservice.authenticationservice.com.service;

import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest loginRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest, String username);
}
