package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.dto.OfferCreateDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;

import java.util.List;

public interface OfferService {

    List<OfferSummaryDTO> getAllOffers();

    void createOffer(OfferCreateDTO offerCreateDTO);

    void deleteOffer(Long offerId);

    OfferDetailsDTO getOfferDetails(Long id);

}
