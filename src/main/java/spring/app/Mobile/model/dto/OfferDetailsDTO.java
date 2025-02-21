package spring.app.Mobile.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class OfferDetailsDTO extends OfferValidationDTO {

    private Long id;
    private String sellerUsername;
    private Instant created;
    private Instant updated;

    private List<String> imageUrls;
    private List<MultipartFile> newImages;
    private List<Long> removeImagesId;

}
