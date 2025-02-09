package spring.app.Mobile.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class OfferSummaryDTO extends OfferBaseDTO {

    private String sellerUsername;
}
