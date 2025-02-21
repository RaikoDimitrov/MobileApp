package spring.app.Mobile.model.dto;

import jakarta.mail.Multipart;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OfferAddDTO extends OfferValidationDTO {

    @NotEmpty(message = "Please upload images")
    private List<Multipart> images;

}
