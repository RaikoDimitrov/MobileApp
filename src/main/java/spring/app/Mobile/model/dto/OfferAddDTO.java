package spring.app.Mobile.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferAddDTO extends OfferValidationDTO {

    private List<MultipartFile> images = new ArrayList<>();

}
