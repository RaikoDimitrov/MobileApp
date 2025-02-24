package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferAddDTO extends OfferValidationDTO {

    @NotEmpty(message = "Please upload at least one image")
    private List<MultipartFile> images;

}
