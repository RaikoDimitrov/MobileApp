package spring.app.Mobile.service.impl;

import org.springframework.stereotype.Service;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.service.interfaces.BrandService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<BrandEntity> getAllBrands() {

        return brandRepository.findAll();
    }

    @Override
    public List<BrandEntity> initializeBrands() {
        List<BrandEntity> brands = new ArrayList<>();
        if (brandRepository.count() == 0) {
            brands.add(BrandEntity.builder().name("Ford").build());
            brands.add(BrandEntity.builder().name("Mercedes").build());
            brands.add(BrandEntity.builder().name("BMW").build());
            brands.add(BrandEntity.builder().name("Audi").build());
            brands.forEach(this::setCurrentTimeStamps);
        }
        return brandRepository.saveAll(brands);
    }

    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }
}
