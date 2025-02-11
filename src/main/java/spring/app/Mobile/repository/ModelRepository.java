package spring.app.Mobile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, Long> {
    long countByBrandEntity(BrandEntity brandEntity);

    List<ModelEntity> findByName(String modelName);

    @Query("SELECT m.name FROM ModelEntity m WHERE m.brandEntity.name = :brandName")
    List<String> findByBrandEntity_Name(@Param("brandName")String brandName);
}
