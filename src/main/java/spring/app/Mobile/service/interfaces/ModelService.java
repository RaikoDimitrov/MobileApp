package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;

import java.util.List;

public interface ModelService {
    List<String> getModelsByBrandName(String brandName);

    void populateModels();
}
