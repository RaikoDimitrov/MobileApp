package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

}
