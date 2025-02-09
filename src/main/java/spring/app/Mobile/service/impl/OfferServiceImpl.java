package spring.app.Mobile.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;
import spring.app.Mobile.repository.OfferRepository;
import spring.app.Mobile.service.interfaces.OfferService;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final RestClient offerRestClient;

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, @Qualifier("offerRestClient") RestClient offerRestClient) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
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
    public void createOffer(OfferAddDTO offerAddDTO) {

    }

    @Override
    public void deleteOffer(Long offerId) {

    }

    @Override
    public OfferDetailsDTO getOfferDetails(Long id) {
        return null;
    }

}
