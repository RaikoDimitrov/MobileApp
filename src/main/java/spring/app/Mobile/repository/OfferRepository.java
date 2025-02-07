package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.app.Mobile.model.entity.OfferEntity;

@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {

}
