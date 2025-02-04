package spring.app.Mobile.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.app.Mobile.service.interfaces.BrandService;
import spring.app.Mobile.service.interfaces.ModelService;
import spring.app.Mobile.service.interfaces.UserService;

@Component
public class DBInit implements CommandLineRunner {

    private final BrandService brandService;
    private final ModelService modelService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInit(BrandService brandService, ModelService modelService, UserService userService, PasswordEncoder passwordEncoder) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {

        brandService.initializeBrands();
        modelService.initializeModels();
        userService.initializeUsers();
    }

}
