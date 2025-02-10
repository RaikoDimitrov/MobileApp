package spring.app.Mobile.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;
import spring.app.Mobile.model.entity.BrandEntity;
import spring.app.Mobile.model.entity.ModelEntity;
import spring.app.Mobile.model.entity.OfferEntity;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.repository.BrandRepository;
import spring.app.Mobile.repository.ModelRepository;
import spring.app.Mobile.repository.OfferRepository;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.service.interfaces.OfferService;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final ModelMapper offerMapper;
    private final RestClient offerRestClient;

    public OfferServiceImpl(OfferRepository offerRepository, BrandRepository brandRepository, ModelRepository modelRepository, UserRepository userRepository, ModelMapper offerMapper, @Qualifier("offerRestClient") RestClient offerRestClient) {
        this.offerRepository = offerRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
        this.offerMapper = offerMapper;
        this.offerRestClient = offerRestClient;
    }


    @Override
    public List<OfferSummaryDTO> getAllOffers() {

        return offerRestClient.get()
                .uri("/offers")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Override
    public OfferAddDTO createOffer(OfferAddDTO offerAddDTO) {
        OfferEntity mappedOfferEntity = map(offerAddDTO);
        OfferEntity savedOffer = offerRepository.save(mappedOfferEntity);
        OfferAddDTO responseDTO = offerMapper.map(savedOffer, OfferAddDTO.class);

        offerRestClient.post()
                .uri("/offers")
                .body(responseDTO)
                .retrieve();

        return responseDTO;
    }

    @Override
    public void deleteOffer(Long offerId) {

    }

    @Override
    public OfferDetailsDTO getOfferDetails(Long id) {
        return null;
    }

    //mapping
    private OfferEntity map(OfferAddDTO offerAddDTO) {
        String username = getLoggedUsername();
        UserEntity seller = userRepository.findByUsername(username);
        BrandEntity brandEntity = brandRepository.findByName(offerAddDTO.getBrandName())
                .orElseGet(() -> {
                    BrandEntity newBrand = new BrandEntity();
                    newBrand.setName(offerAddDTO.getBrandName());
                    return brandRepository.save(newBrand);
                });
        ModelEntity modelEntity = modelRepository.findByName(offerAddDTO.getModelName())
                .stream()
                .filter(model -> model.getBrandEntity().getName().equals(offerAddDTO.getBrandName()))
                .findFirst()
                .orElseGet(() -> {
                    ModelEntity newModel = new ModelEntity();
                    newModel.setName(offerAddDTO.getModelName());
                    return modelRepository.save(newModel);
                });
        OfferEntity mappedOfferEntity = offerMapper.map(offerAddDTO, OfferEntity.class);
        mappedOfferEntity.setSellerEntity(seller);
        mappedOfferEntity.setBrandEntity(brandEntity);
        mappedOfferEntity.setModelEntity(modelEntity);
        return mappedOfferEntity;
    }

    private String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails user) {
            return user.getUsername();
        }
        throw new UsernameNotFoundException("No logged-in user found.");
    }
}
