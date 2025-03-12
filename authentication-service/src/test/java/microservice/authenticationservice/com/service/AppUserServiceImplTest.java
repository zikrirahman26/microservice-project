package microservice.authenticationservice.com.service;

import microservice.authenticationservice.com.dto.AppUserResponse;
import microservice.authenticationservice.com.dto.RegistrationRequest;
import microservice.authenticationservice.com.entity.AppUser;
import microservice.authenticationservice.com.model.AppUserRole;
import microservice.authenticationservice.com.repository.AppUserRepository;
import microservice.authenticationservice.com.service.impl.AppUserServiceImpl;
import microservice.authenticationservice.com.validation.ValidationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ValidationRequest validationRequest;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AppUserServiceImpl appUserServiceImpl;

    private RegistrationRequest registrationRequest;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("testuser");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setFullName("Test User");
        registrationRequest.setPhoneNumber("1234567890");
        registrationRequest.setAppUserRole(AppUserRole.USER);

        appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setEmail("test@example.com");
        appUser.setPassword("encodedPassword");
        appUser.setFullName("Test User");
        appUser.setPhoneNumber("1234567890");
        appUser.setAppUserRole(AppUserRole.USER);
        appUser.setStatus("active");
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        when(appUserRepository.findByUsername("testuser")).thenReturn(Optional.of(appUser));

        UserDetails userDetails = appUserServiceImpl.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        when(appUserRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> appUserServiceImpl.loadUserByUsername("nonexistentuser"));
    }

    @Test
    void userRegistration_ValidRequest_ReturnsAppUserResponse() {
        when(appUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(appUser);

        AppUserResponse response = appUserServiceImpl.userRegistration(registrationRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(validationRequest).validationRequest(registrationRequest);
    }

    @Test
    void userRegistration_UsernameExists_ThrowsBadRequest() {
        when(appUserRepository.existsByUsername("testuser")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> appUserServiceImpl.userRegistration(registrationRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Username is already in use", exception.getReason());
    }

    @Test
    void userRegistration_EmailExists_ThrowsBadRequest() {
        when(appUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(appUserRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> appUserServiceImpl.userRegistration(registrationRequest));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email is already in use", exception.getReason());
    }
}