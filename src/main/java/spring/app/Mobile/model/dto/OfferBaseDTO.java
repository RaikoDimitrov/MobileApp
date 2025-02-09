package spring.app.Mobile.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class OfferBaseDTO {
    private Long id;
    private String brandName;
    private String modelName;
    private int price;
    private int mileage;
    private String description;
}
