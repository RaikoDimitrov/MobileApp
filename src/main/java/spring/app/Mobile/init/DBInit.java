package spring.app.Mobile.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.service.interfaces.BrandService;
import spring.app.Mobile.service.interfaces.ModelService;

import java.time.Instant;
import java.util.List;

@Component
public class DBInit implements CommandLineRunner {

    private final BrandService brandService;
    private final ModelService modelService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInit(BrandService brandService, ModelService modelService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }

    @Override
    public void run(String... args) throws Exception {

        brandService.initializeBrands();
        modelService.initializeModels();

        System.out.println("Initializing admin user...");

        UserEntity admin = UserEntity.builder()
                .firstName("Raiko")
                .lastName("Dimitrov")
                .username("freddy")
                .email("freddy98@abv.bg")
                .password(passwordEncoder.encode("123123"))
                .build();
        try {
            setCurrentTimeStamps(admin);
            userRepository.save(admin);
            System.out.println("Admin user saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
