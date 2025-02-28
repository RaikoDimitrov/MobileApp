package spring.app.Mobile.service.impl;

import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger offerLogger = LoggerFactory.getLogger(OfferServiceImpl.class);

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
    public void deleteOffer(Long offerId) {
        offerRepository.deleteById(offerId);
    }

    @Override
    public OfferDetailsDTO getOfferDetails(Long id) {
        Optional<OfferEntity> offerEntityById = offerRepository.findById(id);
        if (offerEntityById.isEmpty()) {
            System.out.println("Offer not found!");
            return null;
        }
        OfferEntity offerEntity = offerEntityById.get();
        OfferDetailsDTO map = offerMapper.map(offerEntity, OfferDetailsDTO.class);
        return map;
    }

    @Transactional
    @Override
    public void updateOffer(Long offerId, OfferDetailsDTO offerDetailsDTO) {

        OfferEntity offerById = offerRepository.findById(offerId).
                orElseThrow(() -> new ResourceNotFoundException("Offer not found!"));
        Instant created = offerById.getCreated();
        List<String> updatedImagesUrls = new ArrayList<>(offerById.getImageUrls());

        //remove images
        List<String> removedImages = Optional.ofNullable(offerDetailsDTO.getRemoveImagesId()).orElse(Collections.emptyList());
        if (!removedImages.isEmpty()) {
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
        List<MultipartFile> newImages = Optional.ofNullable(offerDetailsDTO.getNewImages())
                .orElse(Collections.emptyList())
                .stream().filter(file -> file != null && !file.isEmpty())
                .collect(Collectors.toList());

        if (!newImages.isEmpty() && newImages.get(0) != null) {
            for (MultipartFile file : newImages) {
                String uploadedUrl = cloudinaryService.uploadImage(file);
                String publicIdFromUrl = cloudinaryService.extractPublicIdFromUrl(uploadedUrl);
                boolean imageExistsInDB = updatedImagesUrls
                        .stream()
                        .anyMatch(existingUrl -> cloudinaryService.extractPublicIdFromUrl(existingUrl).equals(publicIdFromUrl));
                if (!imageExistsInDB) {
                    updatedImagesUrls.add(uploadedUrl);
                }
            }
        }

        if (updatedImagesUrls.isEmpty()) throw new RuntimeException("Please upload at least one image");

        Integer mainImageIndex = offerDetailsDTO.getMainImageIndex();
        if (mainImageIndex != null
                && mainImageIndex < updatedImagesUrls.size()
                && mainImageIndex >= 0) {
            offerDetailsDTO.setMainImageUrl(updatedImagesUrls.get(mainImageIndex));
        } else {
            offerById.setMainImageUrl(updatedImagesUrls.get(0));
        }

        offerMapper.map(offerDetailsDTO, offerById);
        offerById.setImageUrls(updatedImagesUrls);
        offerById.setMainImageUrl(offerDetailsDTO.getMainImageUrl());
        offerById.setCreated(created);
        offerById.setUpdated(Instant.now());
        offerRepository.save(offerById);
    }

    @Override
    public OfferAddDTO createOffer(OfferAddDTO offerAddDTO) {

        if (offerAddDTO.getImages() == null || offerAddDTO.getImages().isEmpty() || offerAddDTO.getImages().get(0).isEmpty()) {
            throw new IllegalArgumentException("No images uploaded");
        }

        List<String> imageUrls = cloudinaryService.uploadImages(offerAddDTO.getImages());

        OfferEntity mappedOfferEntity = map(offerAddDTO);

        if (offerAddDTO.getMainImageIndex() != null
                && offerAddDTO.getMainImageIndex() < imageUrls.size()
                && offerAddDTO.getMainImageIndex() >= 0) {
            mappedOfferEntity.setMainImageUrl(imageUrls.get(offerAddDTO.getMainImageIndex()));
        } else if (!imageUrls.isEmpty()) {
            mappedOfferEntity.setMainImageUrl(imageUrls.get(0));
        } else {
            throw new RuntimeException("Please upload images");
        }

        mappedOfferEntity.setImageUrls(imageUrls);
        OfferEntity savedOffer = offerRepository.save(mappedOfferEntity);
        OfferAddDTO responseDTO = offerMapper.map(savedOffer, OfferAddDTO.class);
        return responseDTO;
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

        OfferEntity mappedOfferEntity = offerMapper.map(offerAddDTO, OfferEntity.class);
        mappedOfferEntity.setSellerEntity(sellerUsername);
        mappedOfferEntity.setBrandEntity(brandEntity);
        mappedOfferEntity.setModelEntity(modelEntity);

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
