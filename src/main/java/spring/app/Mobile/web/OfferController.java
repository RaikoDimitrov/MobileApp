package spring.app.Mobile.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;
import spring.app.Mobile.service.interfaces.BrandService;
import spring.app.Mobile.service.interfaces.ModelService;
import spring.app.Mobile.service.interfaces.OfferService;

@Controller
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;
    private final ModelService modelService;
    private final BrandService brandService;

    public OfferController(OfferService offerService, ModelService modelService, BrandService brandService) {
        this.offerService = offerService;
        this.modelService = modelService;
        this.brandService = brandService;
    }

    @ModelAttribute
    public void populateEnums(Model model) {
        model.addAttribute("allVehicleTypes", VehicleTypeEnum.values());
        model.addAttribute("allEngineTypes", EngineTypeEnum.values());
        model.addAttribute("allTransmissionTypes", TransmissionTypeEnum.values());
        model.addAttribute("allChassisTypes", ChassisTypeEnum.values());
    }

    @RequestMapping("/all")
    public String getAllOffers(Model model) {
        model.addAttribute("allOffers", offerService.getAllOffers());
        return "offers";
    }

    @GetMapping("/add")
    public String newOffer(Model model) {
        if (!model.containsAttribute("offerAddDTO")) {
            model.addAttribute("offerAddDTO", OfferAddDTO.empty());
        }
        model.addAttribute("brands", brandService.getAllBrands());
        return "offer-add";
    }

    @PostMapping("/add")
    public String addOffer(@Valid @ModelAttribute("offerAddDTO") OfferAddDTO offerAddDTO, Model model, BindingResult result, RedirectAttributes rAtt) {
        if (result.hasErrors()) {
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("models", modelService.getModelsByBrandName(offerAddDTO.getBrandName()));
            return "offers-add";
        }
        offerService.createOffer(offerAddDTO);
        rAtt.addFlashAttribute("successMessage", "Offer added successfully!");
        return "redirect:/offers/all";
    }
}
