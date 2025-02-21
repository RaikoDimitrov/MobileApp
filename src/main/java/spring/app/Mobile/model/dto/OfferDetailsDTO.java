package spring.app.Mobile.model.dto;

import jakarta.mail.Multipart;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class OfferDetailsDTO extends OfferValidationDTO {

    private Long id;
    private String sellerUsername;
    private Instant created;
    private Instant updated;

    @NotEmpty(message = "Please upload images")
    private List<Multipart> newImages;
    private List<Long> removeImagesId;

}
