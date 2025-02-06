package spring.app.Mobile.service.impl;

import org.springframework.stereotype.Service;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.ModelRepository;
import spring.app.Mobile.service.interfaces.ModelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;

    public ModelServiceImpl(ModelRepository modelRepository, BrandRepository brandRepository) {
        this.modelRepository = modelRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public List<ModelEntity> initializeModels() {
        Optional<BrandEntity> brandFord = brandRepository.findByName("Ford");
        if (brandFord.isEmpty()) {
            System.out.println("Brand 'Ford' is not found.");
            return Collections.emptyList();
        }
        if (modelRepository.countByBrandEntity(brandFord.get()) > 0) {
            System.out.println("Models for Ford already exist in DB.");
            return Collections.emptyList();
        }

        List<ModelEntity> models = new ArrayList<>();
            models.add(ModelEntity
                    .builder()
                    .category(EngineTypeEnum.CAR)
                    .name("Fiesta").startYear(1976)
                    .brandEntity(brandFord.get())
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/7/7d/2017_Ford_Fiesta_Zetec_Turbo_1.0_Front.jpg")
                    .build());
            models.add(ModelEntity
                    .builder()
                    .category(EngineTypeEnum.CAR)
                    .name("Mustang").startYear(1964)
                    .brandEntity(brandFord.get())
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/00_1812_Ford_Mustang_1969.jpg")
                    .build());
            models.add(ModelEntity
                    .builder()
                    .category(EngineTypeEnum.TRUCK)
                    .name("Raptor").startYear(2010)
                    .brandEntity(brandFord.get())
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/e/ee/Ford_F-150_SVT_Raptor_2011_%2815245966030%29.jpg")
                    .build());

        models.forEach(this::setCurrentTimeStamps);
        return modelRepository.saveAll(models);
    }

    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }
}
