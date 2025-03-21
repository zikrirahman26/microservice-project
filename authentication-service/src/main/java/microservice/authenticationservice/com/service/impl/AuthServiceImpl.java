package microservice.authenticationservice.com.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;
import microservice.authenticationservice.com.entity.AppUser;
import microservice.authenticationservice.com.repository.AppUserRepository;
import microservice.authenticationservice.com.service.AuthService;
import microservice.authenticationservice.com.utils.JwtGenerator;
import microservice.authenticationservice.com.validation.ValidationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;
    private final ValidationRequest validationRequest;
    private final JwtGenerator jwtGenerator;

    @Transactional
    @Override
    public TokenResponse login(LoginRequest loginRequest) {

        validationRequest.validationRequest(loginRequest);

        AppUser appUser = appUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + loginRequest.getUsername() + " not found"));

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password incorrect");
        }

        String token = jwtGenerator.generateToken(appUser);

        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, String username) {

        validationRequest.validationRequest(changePasswordRequest);

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), appUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password incorrect");
        }

        if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "New password cannot be the same");
        } else if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Confirm password incorrect");
        }

        appUser.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
        appUserRepository.save(appUser);
    }
}