package microservice.authenticationservice.com.service.impl;

import lombok.RequiredArgsConstructor;
import microservice.authenticationservice.com.dto.RegistrationRequest;
import microservice.authenticationservice.com.dto.AppUserResponse;
import microservice.authenticationservice.com.entity.AppUser;
import microservice.authenticationservice.com.repository.AppUserRepository;
import microservice.authenticationservice.com.service.AppUserService;
import microservice.authenticationservice.com.validation.ValidationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final ValidationRequest validationRequest;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .build();
    }

    @Override
    public AppUserResponse userRegistration(RegistrationRequest registrationRequest) {

        validationRequest.validationRequest(registrationRequest);

        if (appUserRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use");
        }

        if (appUserRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        String encodePassword = bCryptPasswordEncoder.encode(registrationRequest.getPassword());
        AppUser appUser = getUserManagement(registrationRequest, encodePassword);

        appUserRepository.save(appUser);
        return userResponse(appUser);
    }

    private static AppUser getUserManagement(RegistrationRequest registrationRequest, String encodePassword) {
        AppUser appUser = new AppUser();
        appUser.setUsername(registrationRequest.getUsername());
        appUser.setEmail(registrationRequest.getEmail());
        appUser.setPassword(encodePassword);
        appUser.setFullName(registrationRequest.getFullName());
        appUser.setPhoneNumber(registrationRequest.getPhoneNumber());
        appUser.setAppUserRole(registrationRequest.getAppUserRole());
        appUser.setStatus("active");
        return appUser;
    }

    private AppUserResponse userResponse(AppUser appUser) {
        return AppUserResponse.builder()
                .username(appUser.getUsername())
                .email(appUser.getEmail())
                .fullName(appUser.getFullName())
                .phoneNumber(appUser.getPhoneNumber())
                .appUserRole(appUser.getAppUserRole())
                .status(appUser.getStatus())
                .build();
    }
}