package spring.app.Mobile.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.app.Mobile.service.interfaces.BrandsService;

@Controller
@RequestMapping("/brands")
public class BrandsController {

    private final BrandsService brandsService;

    public BrandsController(BrandsService brandsService) {
        this.brandsService = brandsService;
    }

    @GetMapping("/all")
    public String allBrands(Model model) {
        model.addAttribute("brands", brandsService.getAllBrands());
        return "brands";
    }

}
