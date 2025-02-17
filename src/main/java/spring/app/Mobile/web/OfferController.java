package spring.app.Mobile.web;

import jakarta.validation.Valid;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public void populateEnumsAndBrands(Model model) {
        model.addAttribute("allVehicleTypes", VehicleTypeEnum.values());
        model.addAttribute("allEngineTypes", EngineTypeEnum.values());
        model.addAttribute("allTransmissionTypes", TransmissionTypeEnum.values());
        model.addAttribute("allChassisTypes", ChassisTypeEnum.values());
        model.addAttribute("brands", brandService.getAllBrands());
    }

    @GetMapping("/all")
    public String getAllOffers(Model model) {
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
            model.addAttribute("offerAddDTO", new OfferAddDTO());
        }
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
            model.addAttribute("models", modelService.getModelsByBrandName(offerAddDTO.getBrandName()));
            return "offer-add";
        }
        offerService.createOffer(offerAddDTO);
        rAtt.addFlashAttribute("successMessage", "Offer added successfully!");
        return "redirect:/offers/all";
    }

    @GetMapping("/{id}")
    public String detailsOffer(@PathVariable Long id,
                               Model model,
                               Principal principal) {
        OfferDetailsDTO offerDetails = offerService.getOfferDetails(id);
        if (offerDetails == null || offerDetails.getCreated() == null) {
            model.addAttribute("offerId", id);
            return "offer-not-found";
        }
        Instant createdInstant = offerDetails.getCreated();
        Instant updatedInstant = offerDetails.getUpdated();
        LocalDateTime created = LocalDateTime.ofInstant(createdInstant, ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(updatedInstant, ZoneId.systemDefault());
        String username = null;
        if (principal != null) {
            username = principal.getName();
        }
        model.addAttribute("created", created);
        model.addAttribute("updated", updated);
        model.addAttribute("username", username);
        model.addAttribute("offerDetails", offerDetails);
        return "details";
    }

    @DeleteMapping("/{id}")
    public String deleteOffer(@PathVariable Long id, RedirectAttributes rAtt) {
        offerService.deleteOffer(id);
        rAtt.addFlashAttribute("successMessage", "Offer deleted successfully!");
        return "redirect:/offers/all";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        OfferDetailsDTO offerUpdate = offerService.getOfferDetails(id);
        if (offerUpdate == null) return "offer-not-found";
        model.addAttribute("offerUpdate", offerUpdate);
        model.addAttribute("models", modelService.getModelsByBrandName(offerUpdate.getBrandName()));
        return "update";
    }

    @PatchMapping("/update/{id}")
    public String updateOffer(@PathVariable Long id,
                              @Valid @ModelAttribute("offerUpdate") OfferDetailsDTO offerDetailsDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes rAtt) {
        if (result.hasErrors()) {
            model.addAttribute("offerUpdate", offerDetailsDTO);
            model.addAttribute("models", modelService.getModelsByBrandName(offerDetailsDTO.getBrandName()));
            return "update";
        }
        offerService.updateOffer(id, offerDetailsDTO);
        rAtt.addFlashAttribute("successMessage", "Changes saved!");
        return "redirect:/offers/{id}";
    }

}
