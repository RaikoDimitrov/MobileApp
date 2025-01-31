package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.app.Mobile.model.entity.UserEntity;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
}
