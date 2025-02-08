package spring.app.Mobile.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<OfferSummaryDTO> getAllOffers() {
        return null;
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
