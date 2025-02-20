package spring.app.Mobile.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class OfferDetailsDTO extends OfferValidationDTO {

    private Long id;
    private String sellerUsername;
    private Instant created;
    private Instant updated;

}
