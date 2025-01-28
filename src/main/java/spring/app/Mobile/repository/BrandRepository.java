package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.app.Mobile.model.entity.BrandEntity;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

}
