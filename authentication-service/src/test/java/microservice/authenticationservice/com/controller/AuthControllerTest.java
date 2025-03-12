package microservice.authenticationservice.com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservice.authenticationservice.com.dto.ApiResponse;
import microservice.authenticationservice.com.dto.ChangePasswordRequest;
import microservice.authenticationservice.com.dto.LoginRequest;
import microservice.authenticationservice.com.dto.TokenResponse;
import microservice.authenticationservice.com.service.AuthService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginRequest loginRequest;
    private TokenResponse tokenResponse;
    private ChangePasswordRequest changePasswordRequest;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password");

        tokenResponse = TokenResponse.builder()
                .token("testToken")
                .build();

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("oldPass123");
        changePasswordRequest.setNewPassword("newPass456");
    }

    @Test
    void login_ValidRequest_ReturnsOk() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/api-auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void login_ValidRequest_ReturnsCorrectResponse() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        String expectedResponse = objectMapper.writeValueAsString(ApiResponse.<TokenResponse>builder()
                .data(tokenResponse)
                .message("User login successful")
                .build());

        mockMvc.perform(post("/api-auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void changePassword_ValidRequest_ReturnsCorrectResponse() throws Exception {
        String username = "testUser";

        doNothing().when(authService).changePassword(any(ChangePasswordRequest.class), any(String.class));

        String expectedResponse = objectMapper.writeValueAsString(ApiResponse.<String>builder()
                .data(null)
                .message("Password changed successfully")
                .build());

        mockMvc.perform(patch("/api-auth/change-password/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
