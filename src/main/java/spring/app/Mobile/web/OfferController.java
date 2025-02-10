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
import spring.app.Mobile.service.interfaces.OfferService;

@Controller
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @ModelAttribute("allVehicleTypes")
    public VehicleTypeEnum[] allVehicleTypes() {
        return VehicleTypeEnum.values();
    }

    @ModelAttribute("allEngineTypes")
    public EngineTypeEnum[] allEngineTypes() {
        return EngineTypeEnum.values();
    }

    @ModelAttribute("allTransmissionTypes")
    public TransmissionTypeEnum[] allTransmissionTypes() {
        return TransmissionTypeEnum.values();
    }

    @ModelAttribute("allEngineTypes")
    public ChassisTypeEnum[] allChassisTypes() {
        return ChassisTypeEnum.values();
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
        return "offer-add";
    }

    @PostMapping("/add")
    public String addOffer(@Valid OfferAddDTO offerAddDTO, BindingResult result, RedirectAttributes rAtt) {
        if (result.hasErrors()) {
            rAtt.addFlashAttribute("offerAddDTO", offerAddDTO);
            rAtt.addFlashAttribute("org.springframework.validation.BindingResult.offerAddDTO", result);
            return "redirect:/offers/add";
        }
        offerService.createOffer(offerAddDTO);
        return "redirect:/offers/all";
    }
}
