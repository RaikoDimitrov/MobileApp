package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.entity.BrandEntity;

import java.util.List;

public interface BrandService {

    List<BrandEntity> getAllBrands();

    List<BrandEntity> initializeBrands();
}
