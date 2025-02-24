package spring.app.Mobile.service.impl;

import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.common.protocol.types.Field;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;
import spring.app.Mobile.model.entity.*;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.ModelRepository;
import spring.app.Mobile.repository.OfferRepository;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.service.interfaces.CloudinaryService;
import spring.app.Mobile.service.interfaces.OfferService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final ModelMapper offerMapper;
    private final CloudinaryService cloudinaryService;
    private final RestClient offerRestClient;

    public OfferServiceImpl(OfferRepository offerRepository, BrandRepository brandRepository, ModelRepository modelRepository, UserRepository userRepository, ModelMapper offerMapper, CloudinaryService cloudinaryService, @Qualifier("offerRestClient") RestClient offerRestClient) {
        this.offerRepository = offerRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
        this.offerMapper = offerMapper;
        this.cloudinaryService = cloudinaryService;
        this.offerRestClient = offerRestClient;
    }

    @Override
    public List<OfferSummaryDTO> getAllOffers() {

        /* web client
        List<OfferEntity> offerEntities = offerRestClient.get()
                .uri("/offers")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        System.out.println("API response : " + offerEntities);
        if (offerEntities == null || offerEntities.isEmpty()) return Collections.emptyList();*/

        List<OfferEntity> offerEntities = offerRepository.findAll();
        if (offerEntities.isEmpty()) return Collections.emptyList();

        return offerEntities.stream()
                .map(offerEntity -> offerMapper.map(offerEntity, OfferSummaryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OfferAddDTO createOffer(OfferAddDTO offerAddDTO) {
        OfferEntity mappedOfferEntity = map(offerAddDTO);
        OfferEntity savedOffer = offerRepository.save(mappedOfferEntity);
        OfferAddDTO responseDTO = offerMapper.map(savedOffer, OfferAddDTO.class);

    /* web client
        offerRestClient.post()
                .uri("/offers")
                .body(responseDTO)
                .retrieve();*/

        return responseDTO;
    }

    @Override
    public void deleteOffer(Long offerId) {
        System.out.println("delete method is called with id: " + offerId);
        offerRepository.deleteById(offerId);
    }

    @Transactional
    @Override
    public void updateOffer(Long offerId, OfferDetailsDTO offerDetailsDTO) {
        System.out.println("Existing images: " + offerDetailsDTO.getImageUrls());
        System.out.println("New images: " + offerDetailsDTO.getNewImages());
        System.out.println("Removed images: " + offerDetailsDTO.getRemoveImagesId());


        OfferEntity offerById = offerRepository.findById(offerId).
                orElseThrow(() -> new ResourceNotFoundException("Offer not found!"));
        Instant created = offerById.getCreated();

        List<String> updatedImagesUrls = new ArrayList<>(offerById.getImageUrls());

        //remove images
        List<String> removedImages = offerDetailsDTO.getRemoveImagesId();
        if (removedImages != null && !removedImages.isEmpty()) {
            List<String> toRemove = new ArrayList<>();
            for (String imageUrl : removedImages) {
                try {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(imageUrl);
                    if (publicId != null) {
                        cloudinaryService.deleteImage(publicId);
                        toRemove.add(imageUrl);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete image from cloudinary: " + imageUrl, e);
                }
            }
            updatedImagesUrls.removeAll(toRemove);
        }

        //upload images
        List<MultipartFile> newImages = offerDetailsDTO.getNewImages();
        if (newImages != null && !newImages.isEmpty()) {
            List<String> newImagesUrls = cloudinaryService.uploadImages(newImages);
            updatedImagesUrls.addAll(newImagesUrls);
        }
        if (updatedImagesUrls.isEmpty()) throw  new RuntimeException("Please upload at least one image");

        if (offerDetailsDTO.getMainImageIndex() != null
                && offerDetailsDTO.getMainImageIndex() < offerById.getImageUrls().size()
                && offerDetailsDTO.getMainImageIndex() >= 0) {
            offerById.setMainImageUrl(updatedImagesUrls.get(offerDetailsDTO.getMainImageIndex()));
        } else if (!updatedImagesUrls.isEmpty()){
            offerById.setMainImageUrl(updatedImagesUrls.get(0));
        } else {
            throw new RuntimeException("Please upload images");
        }
        offerById.setImageUrls(updatedImagesUrls);

        offerMapper.map(offerDetailsDTO, offerById);
        offerById.setCreated(created);
        offerById.setUpdated(Instant.now());
        offerRepository.save(offerById);
    }

    @Override
    public OfferDetailsDTO getOfferDetails(Long id) {
        Optional<OfferEntity> offerEntityById = offerRepository.findById(id);
        if (offerEntityById.isEmpty()) {
            System.out.println("Offer not found!");
            return null;
        }
        OfferEntity offerEntity = offerEntityById.get();
        return offerMapper.map(offerEntity, OfferDetailsDTO.class);
    }

    //mapping dto -> entity
    private OfferEntity map(OfferAddDTO offerAddDTO) {
        String username = getLoggedUsername();
        UserEntity sellerUsername = userRepository.findByUsername(username);

        BrandEntity brandEntity = brandRepository.findByName(offerAddDTO.getBrandName());
        brandEntity.setName(offerAddDTO.getBrandName());
        brandRepository.save(brandEntity);

        ModelEntity modelEntity = modelRepository.findByName(offerAddDTO.getModelName())
                .stream()
                .filter(model -> model.getBrandEntity().getName().equals(offerAddDTO.getBrandName()))
                .findFirst()
                .orElseGet(() -> {
                    ModelEntity newModel = new ModelEntity();
                    newModel.setName(offerAddDTO.getModelName());
                    newModel.setBrandEntity(brandEntity);
                    return modelRepository.save(newModel);
                });

        List<String> imageUrls = cloudinaryService.uploadImages(offerAddDTO.getImages());

        OfferEntity mappedOfferEntity = offerMapper.map(offerAddDTO, OfferEntity.class);
        mappedOfferEntity.setSellerEntity(sellerUsername);
        mappedOfferEntity.setBrandEntity(brandEntity);
        mappedOfferEntity.setModelEntity(modelEntity);

        mappedOfferEntity.setImageUrls(imageUrls);
        if (offerAddDTO.getMainImageIndex() != null
                && offerAddDTO.getMainImageIndex() < imageUrls.size()
                && offerAddDTO.getMainImageIndex() >= 0) {
            mappedOfferEntity.setMainImageUrl(imageUrls.get(offerAddDTO.getMainImageIndex()));
        } else if (!imageUrls.isEmpty()){
            mappedOfferEntity.setMainImageUrl(imageUrls.get(0));
        } else {
            throw new RuntimeException("Please upload images");
        }

        setCurrentTimeStamps(mappedOfferEntity);
        return mappedOfferEntity;
    }

    private String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails user) {
            return user.getUsername();
        }
        throw new UsernameNotFoundException("No logged-in user found.");
    }

    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }
}
