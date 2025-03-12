package microservice.authenticationservice.com.service;

import microservice.authenticationservice.com.dto.RegistrationRequest;
import microservice.authenticationservice.com.dto.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AppUserService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    AppUserResponse userRegistration(RegistrationRequest registrationRequest);
}
