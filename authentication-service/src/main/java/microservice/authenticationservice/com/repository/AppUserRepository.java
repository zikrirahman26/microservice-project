package microservice.authenticationservice.com.repository;

import microservice.authenticationservice.com.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
