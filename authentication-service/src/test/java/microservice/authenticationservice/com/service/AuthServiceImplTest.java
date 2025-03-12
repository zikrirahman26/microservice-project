package microservice.authenticationservice.com.service;

import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;
import microservice.authenticationservice.com.entity.AppUser;
import microservice.authenticationservice.com.model.AppUserRole;
import microservice.authenticationservice.com.repository.AppUserRepository;
import microservice.authenticationservice.com.service.impl.AuthServiceImpl;
import microservice.authenticationservice.com.utils.JwtGenerator;
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

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private LoginRequest loginRequest;
    private AppUser appUser;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setPassword("encodedPassword");
        appUser.setAppUserRole(AppUserRole.USER);

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword");
    }

    @Test
    void login_ValidCredentials_ReturnsTokenResponse() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        when(jwtGenerator.generateToken(appUser)).thenReturn("testToken");

        TokenResponse response = authServiceImpl.login(loginRequest);

        assertNotNull(response);
        assertEquals("testToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_InvalidCredentials_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));

        assertThrows(ResponseStatusException.class, () -> authServiceImpl.login(loginRequest));
        try{
            authServiceImpl.login(loginRequest);
        }catch(ResponseStatusException e){
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertEquals("Password incorrect", e.getReason());
        }
    }

    @Test
    void login_UserNotFound_ThrowsUsernameNotFoundException() {
        when(appUserRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authServiceImpl.login(new LoginRequest("nonexistentuser", "password")));
    }

    @Test
    void changePassword_ValidRequest_PasswordChanged() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(bCryptPasswordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        authServiceImpl.changePassword(changePasswordRequest, "testuser");

        assertEquals("newEncodedPassword", appUser.getPassword());
        verify(appUserRepository).save(appUser);
    }

    @Test
    void changePassword_UserNotFound_ThrowsUsernameNotFoundException() {
        when(appUserRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "nonexistentuser"));
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "testuser"));
        try{
            authServiceImpl.changePassword(changePasswordRequest, "testuser");
        }catch(ResponseStatusException e){
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertEquals("Old password incorrect", e.getReason());
        }
    }

    @Test
    void changePassword_SameNewPassword_ThrowsUnauthorized() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));
        when(bCryptPasswordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        changePasswordRequest.setNewPassword("oldPassword");

        assertThrows(ResponseStatusException.class, () -> authServiceImpl.changePassword(changePasswordRequest, "testuser"));
        try{
            authServiceImpl.changePassword(changePasswordRequest, "testuser");
        }catch(ResponseStatusException e){
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
            assertEquals("New password cannot be the same", e.getReason());
        }
    }
}
