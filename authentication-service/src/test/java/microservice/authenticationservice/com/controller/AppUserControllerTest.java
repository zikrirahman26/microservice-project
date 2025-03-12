package microservice.authenticationservice.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.authenticationservice.com.dto.ApiResponse;
import microservice.authenticationservice.com.dto.AppUserResponse;
import microservice.authenticationservice.com.dto.RegistrationRequest;
import microservice.authenticationservice.com.model.AppUserRole;
import microservice.authenticationservice.com.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AppUserControllerTest {

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private AppUserController appUserController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegistrationRequest registrationRequest;
    private AppUserResponse appUserResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(appUserController).build();
        objectMapper = new ObjectMapper();

        registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername("testuser");
        registrationRequest.setEmail("test@example.com");
        registrationRequest.setPassword("password");
        registrationRequest.setFullName("Test User");
        registrationRequest.setPhoneNumber("1234567890");
        registrationRequest.setAppUserRole(AppUserRole.USER);

        appUserResponse = AppUserResponse.builder()
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("1234567890")
                .appUserRole(AppUserRole.USER)
                .status("active")
                .build();
    }

    @Test
    void userRegistration_ValidRequest_ReturnsOk() throws Exception {
        when(appUserService.userRegistration(any(RegistrationRequest.class))).thenReturn(appUserResponse);

        mockMvc.perform(post("/api-users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void userRegistration_ValidRequest_ReturnsCorrectResponse() throws Exception {
        when(appUserService.userRegistration(any(RegistrationRequest.class))).thenReturn(appUserResponse);

        String expectedResponse = objectMapper.writeValueAsString(ApiResponse.<AppUserResponse>builder()
                .data(appUserResponse)
                .message("User registration successfully")
                .build());

        mockMvc.perform(post("/api-users/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(content().json(expectedResponse));
    }
}
