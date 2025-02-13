package spring.app.Mobile.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class OfferBaseDTO {
    private Long id;
    private String description;
    private Integer mileage;
    private Integer price;
    private Integer horsePower;
    private String brandName;
    private String modelName;
    private Integer year;
    private String sellerUsername;
    private String imageUrl;
    private Instant created;
    private Instant updated;
}
