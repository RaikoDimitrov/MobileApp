package spring.app.Mobile.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class OfferBaseDTO {
    private Long id;
    private String brandName;
    private String modelName;
    private Integer price;
    private Integer mileage;
    private String description;
    private String sellerUsername;
    private String imageUrl;

}
