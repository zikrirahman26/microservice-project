package microservice.authenticationservice.com.service;

import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;
import microservice.authenticationservice.com.entity.AppUser;
import microservice.authenticationservice.com.repository.AppUserRepository;
import microservice.authenticationservice.com.service.impl.AuthServiceImpl;
import microservice.authenticationservice.com.utils.JwtGenerator;
import microservice.authenticationservice.com.validation.ValidationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private ValidationRequest validationRequest;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private LoginRequest loginRequest;
    private AppUser appUser;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testUser", "password");

        appUser = new AppUser();
        appUser.setUsername("testUser");
        appUser.setPassword("encodedPassword");

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
        changePasswordRequest.setConfirmPassword("newPassword");
    }

    @Test
    void login_ValidCredentials_ReturnsTokenResponse() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(jwtGenerator.generateToken(appUser)).thenReturn("testToken");

        TokenResponse response = authServiceImpl.login(loginRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(""));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.login(loginRequest));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Password incorrect", exception.getReason());
    }

    @Test
    void login_UserNotFound_ThrowsUsernameNotFoundException() {
        when(appUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authServiceImpl.login(new LoginRequest("nonexistent", "password")));
    }

    @Test
    void changePassword_ValidRequest_PasswordChanged() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        authServiceImpl.changePassword(changePasswordRequest, "testUser");

        assertEquals("newEncodedPassword", appUser.getPassword());
        verify(appUserRepository).save(appUser);
    }

    @Test
    void changePassword_UserNotFound_ThrowsUsernameNotFoundException() {
        when(appUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "nonexistent"));
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "testUser"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Old password incorrect", exception.getReason());
    }

    @Test
    void changePassword_SameNewPassword_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        changePasswordRequest.setNewPassword("oldPassword");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "testUser"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("New password cannot be the same", exception.getReason());
    }

    @Test
    void changePassword_ConfirmPasswordMismatch_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testUser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        changePasswordRequest.setConfirmPassword("wrongPassword");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "testUser"));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Confirm password incorrect", exception.getReason());
    }
}