package spring.app.Mobile.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.service.interfaces.BrandService;

import java.util.List;

@Controller
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/all")
    public String showBrands(Model model) {
        List<BrandEntity> allBrands = brandService.getAllBrands();
        model.addAttribute("brands", allBrands);
        return "brands";
    }

}
