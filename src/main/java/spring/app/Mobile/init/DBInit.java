package spring.app.Mobile.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.ModelRepository;
import spring.app.Mobile.service.interfaces.BrandService;
import spring.app.Mobile.service.interfaces.ModelService;
import spring.app.Mobile.service.interfaces.UserService;

@Component
public class DBInit implements CommandLineRunner {

    private final BrandService brandService;
    private final ModelService modelService;
    private final UserService userService;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInit(BrandService brandService, ModelService modelService, UserService userService, BrandRepository brandRepository, ModelRepository modelRepository, PasswordEncoder passwordEncoder) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.userService = userService;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {

        brandService.populateBrands();
        modelService.populateModels();
        userService.initializeUsers();

    }

}
