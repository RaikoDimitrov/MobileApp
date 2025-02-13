package spring.app.Mobile.service.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.ModelRepository;
import spring.app.Mobile.service.interfaces.ModelService;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;

    public ModelServiceImpl(ModelRepository modelRepository, BrandRepository brandRepository) {
        this.modelRepository = modelRepository;
        this.brandRepository = brandRepository;
    }

    @Override
    public List<String> getModelsByBrandName(String brandName) {
        return modelRepository.findByBrandEntity_Name(brandName);
    }

    @Override
    public void populateModels() {
        List<String> modelsData = loadModelsFromFile("models.txt");
        for (String line : modelsData) {
            String[] parts = line.split(": ");
            String brandName = parts[0];
            String modelName = parts[1];
            BrandEntity brandEntity = brandRepository.findByName(brandName);
            if (!modelRepository.existsByBrandEntityAndName(brandEntity, modelName)) {
                ModelEntity model = new ModelEntity();
                model.setBrandEntity(brandEntity);
                model.setName(modelName);
                modelRepository.save(model);
            }
        }
    }

    private List<String> loadModelsFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
