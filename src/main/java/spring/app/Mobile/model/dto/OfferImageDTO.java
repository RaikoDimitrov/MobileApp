package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class OfferImageDTO {

    @NotNull(message = "Upload at least one image")
    private List<MultipartFile> images;
    private List<String> removeImagesId;
    private Integer mainImageIndex;

}
