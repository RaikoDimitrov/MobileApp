package spring.app.Mobile.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;
import spring.app.Mobile.service.interfaces.BrandService;
import spring.app.Mobile.service.interfaces.ModelService;
import spring.app.Mobile.service.interfaces.OfferService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

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

    @GetMapping("/all")
    public String getAllOffers(Model model) {
        if (model.containsAttribute("successMessage")) {
            System.out.println("Flash msg: " + model.getAttribute("successMessage"));
        } else System.out.println("No flash msg received");
        model.addAttribute("allOffers", offerService.getAllOffers());
        return "offers";
    }

    //fetching models from ajax
    @GetMapping("/models/{brandName}")
    @ResponseBody
    public List<String> getModelsByBrand(@PathVariable String brandName) {
        return modelService.getModelsByBrandName(brandName);
    }

    @GetMapping("/add")
    public String newOffer(Model model) {
        if (!model.containsAttribute("offerAddDTO")) {
            model.addAttribute("offerAddDTO", OfferAddDTO.empty());
        }
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("models", Collections.emptyList());
        return "offer-add";
    }

    @PostMapping("/add")
    public String addOffer(@Valid @ModelAttribute("offerAddDTO") OfferAddDTO offerAddDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes rAtt,
                           Principal principal) {
        if (principal == null) {
            return "redirect:/users/login";
        }
        if (result.hasErrors()) {
            System.out.println("Validation Errors: " + result.getAllErrors());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("models", modelService.getModelsByBrandName(offerAddDTO.getBrandName()));
            return "offer-add";
        }
        offerService.createOffer(offerAddDTO);
        rAtt.addFlashAttribute("successMessage", "Offer added successfully!");
        return "redirect:/offers/all";
    }

    @GetMapping("{id}")
    public String detailsOffer(@ModelAttribute OfferDetailsDTO offerDetailsDTO0) {

        return "details";
    }
}
