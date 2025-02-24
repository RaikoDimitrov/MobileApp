package spring.app.Mobile.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;

import java.util.List;

public interface OfferService {

    List<OfferSummaryDTO> getAllOffers();

    OfferAddDTO createOffer(OfferAddDTO offerAddDTO,
                            List<MultipartFile> images,
                            Integer mainImageIndex,
                            String removeImagesId);

    void deleteOffer(Long offerId);

    void updateOffer(Long offerId, OfferDetailsDTO offerDetailsDTO);

    OfferDetailsDTO getOfferDetails(Long id);

}
