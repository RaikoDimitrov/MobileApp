package spring.app.Mobile.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferDetailsDTO extends OfferValidationDTO {

    private Long id;
    private String sellerUsername;
    private Instant created;
    private Instant updated;

    private String mainImageUrl;
    private List<String> imageUrls;
    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }
    private List<MultipartFile> newImages;
    private List<String> removeImagesId;

}
