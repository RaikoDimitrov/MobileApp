package spring.app.Mobile.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.enums.CategoryEnum;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.repository.BrandRepository;

import java.time.Instant;
import java.util.List;

@Component
public class DBInit implements CommandLineRunner {
    private final BrandRepository brandRepository;

    public DBInit(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (brandRepository.count() == 0) {
            //brand
            BrandEntity ford = BrandEntity
                    .builder()
                    .name("Ford")
                    .created(Instant.now()).build();
            //models
            ModelEntity fiesta = ModelEntity
                    .builder()
                    .category(CategoryEnum.CAR)
                    .name("Fiesta").startYear(1976)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/7/7d/2017_Ford_Fiesta_Zetec_Turbo_1.0_Front.jpg")
                    .build();
            System.out.println(fiesta.getName());
            System.out.println(fiesta.getBrandEntity());

            ModelEntity mustang = ModelEntity
                    .builder()
                    .category(CategoryEnum.CAR)
                    .name("Mustang").startYear(1964)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/4/47/00_1812_Ford_Mustang_1969.jpg")
                    .build();

            ModelEntity raptor = ModelEntity
                    .builder()
                    .category(CategoryEnum.TRUCK)
                    .name("Raptor").startYear(2010)
                    .brandEntity(ford)
                    .imageUrl("https://upload.wikimedia.org/wikipedia/commons/e/ee/Ford_F-150_SVT_Raptor_2011_%2815245966030%29.jpg")
                    .build();

            ford.setModelsEntity(List.of(fiesta, mustang, raptor));
            brandRepository.save(ford);
        }
    }
}
