package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.app.Mobile.model.entity.UserRoleEntity;
import spring.app.Mobile.model.enums.UserRoleEnum;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByRole(UserRoleEnum role);
}
