package spring.app.Mobile.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferImageDTO;
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
import java.util.stream.Collectors;

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
                           BindingResult offerResult,
                           @RequestParam("images") List<MultipartFile> images,
                           @RequestParam(value = "removeImagesId", required = false) List<String> removeImagesId,
                           Model model,
                           RedirectAttributes rAtt,
                           Principal principal) {

        if (principal == null) {
            return "redirect:/users/login";
        }
        //todo: add requestparams into dto and use modelattribute


        // 🔥 Debugging: Log offerAddDTO values
        System.out.println("Brand Name: " + offerAddDTO.getBrandName());
        System.out.println("Model Name: " + offerAddDTO.getModelName());
        System.out.println("Price: " + offerAddDTO.getPrice());
        System.out.println("Mileage: " + offerAddDTO.getMileage());
        System.out.println("Year: " + offerAddDTO.getYear());
        System.out.println("HorsePower: " + offerAddDTO.getHorsePower());
        System.out.println("Description: " + offerAddDTO.getDescription());



        // ✅ Log received images BEFORE filtering
        System.out.println("📸 Received images: " + images.size());
        images.forEach(img -> System.out.println(" - " + img.getOriginalFilename() + " (size: " + img.getSize() + ")"));

        images.forEach(img -> {
            System.out.println("Image Name: " + img.getOriginalFilename());
            System.out.println("Image Size: " + img.getSize());
            System.out.println("Is Empty: " + img.isEmpty());
        });


        // ✅ Log removed images
        if (removeImagesId != null) {
            System.out.println("🗑️ Removed images: " + removeImagesId);
        }


        if (offerResult.hasErrors()) {
            model.addAttribute("offerAddDTO", offerAddDTO);
            model.addAttribute("models", modelService.getModelsByBrandName(offerAddDTO.getBrandName()));
            return "offer-add";
        }

        if (images.isEmpty() || images.get(0).isEmpty()) {
            model.addAttribute("imageError", "Upload at least one image");
            return "offer-add";
        }
        List<MultipartFile> filteredImages = images.stream().filter(image -> removeImagesId == null || !removeImagesId.contains(image.getOriginalFilename()))
                .collect(Collectors.toList());


        // ✅ Log after filtering
        System.out.println("📸 Images after filtering: " + filteredImages.size());

        try {
            offerService.createOffer(offerAddDTO, filteredImages);
        } catch (Exception e) {
            rAtt.addFlashAttribute("error", "An error occurred while uploading offer");
        }
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
                              @ModelAttribute("offerImage") OfferImageDTO offerImageDTO,
                              Model model,
                              RedirectAttributes rAtt) {
        System.out.println("Received DTO: " + offerDetailsDTO);
        System.out.println("New images count: " + (offerDetailsDTO.getNewImages() != null ? offerDetailsDTO.getNewImages().size() : 0));
        System.out.println("Remove images count: " + (offerDetailsDTO.getRemoveImagesId() != null ? offerDetailsDTO.getRemoveImagesId().size() : 0));

        if (result.hasErrors()) {
            model.addAttribute("offerUpdate", offerDetailsDTO);
            return "update";
        }

        try {
            offerService.updateOffer(id, offerDetailsDTO, offerImageDTO);
            rAtt.addFlashAttribute("successMessage", "Changes saved!");
        } catch (Exception e) {
            model.addAttribute("error", "Cannot update without images");
            System.out.println(e.getMessage());
            ;
            return "update";
        }
        return "redirect:/offers/{id}";
    }

}
