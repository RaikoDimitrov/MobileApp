package spring.app.Mobile.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.repository.BrandRepository;

import java.util.List;

@Component
public class DBInit implements CommandLineRunner {
    private final BrandRepository brandRepository;
    private final PasswordEncoder passwordEncoder;

    public DBInit(BrandRepository brandRepository, PasswordEncoder passwordEncoder) {
        this.brandRepository = brandRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (brandRepository.count() == 0) {
            //brand
            BrandEntity ford = BrandEntity
                    .builder()
                    .name("Ford")
                    .build();
            //models
            ModelEntity fiesta = ModelEntity
                    .builder()
                    .category(EngineTypeEnum.CAR)
                    .name("Fiesta").startYear(1976)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/7/7d/2017_Ford_Fiesta_Zetec_Turbo_1.0_Front.jpg")
                    .build();
            System.out.println(fiesta.getName());
            System.out.println(fiesta.getBrandEntity());

            ModelEntity mustang = ModelEntity
                    .builder()
                    .category(EngineTypeEnum.CAR)
                    .name("Mustang").startYear(1964)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/00_1812_Ford_Mustang_1969.jpg")
                    .build();

            ModelEntity raptor = ModelEntity
                    .builder()
                    .category(EngineTypeEnum.TRUCK)
                    .name("Raptor").startYear(2010)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/e/ee/Ford_F-150_SVT_Raptor_2011_%2815245966030%29.jpg")
                    .build();

            ford.setModelsEntity(List.of(fiesta, mustang, raptor));
            brandRepository.save(ford);
        }
    }
}
