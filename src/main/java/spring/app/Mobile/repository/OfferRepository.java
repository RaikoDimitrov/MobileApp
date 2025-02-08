package spring.app.Mobile.repository;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.app.Mobile.model.entity.OfferEntity;

import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {

    @Nullable
    Optional<OfferEntity> findById(Long Id);
}
