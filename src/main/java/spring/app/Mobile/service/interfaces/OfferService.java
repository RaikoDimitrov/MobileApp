package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;

import java.util.List;

public interface OfferService {

    List<OfferSummaryDTO> getAllOffers();

    OfferAddDTO createOffer(OfferAddDTO offerAddDTO);

    void deleteOffer(Long offerId);

    void updateOffer(Long offerId, OfferDetailsDTO offerDetailsDTO);

    OfferDetailsDTO getOfferDetails(Long id);

}
