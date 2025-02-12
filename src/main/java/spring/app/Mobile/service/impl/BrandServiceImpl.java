package spring.app.Mobile.service.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.service.interfaces.BrandService;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final DataSource dataSource;

    public BrandServiceImpl(BrandRepository brandRepository, DataSource dataSource) {
        this.brandRepository = brandRepository;
        this.dataSource = dataSource;
    }

    private List<String> loadBrandNamesFromFile(String fileName) {
        List<String> brandNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                brandNames.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brandNames;
    }

    @Override
    public List<String> getAllBrands() {

        return brandRepository.findAll()
                .stream()
                .map(BrandEntity::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<BrandEntity> getAllBrandsEntities() {
        return brandRepository.findAll();
    }

    @Override
    public void populateBrands() {

        List<String> brandNames = loadBrandNamesFromFile("brands.txt");
        for (String brandName : brandNames) {
            if (!brandRepository.existsByName(brandName)) {
                BrandEntity brand = new BrandEntity();
                brand.setName(brandName);
                brandRepository.save(brand);
            }
        }
    }

    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }
}
