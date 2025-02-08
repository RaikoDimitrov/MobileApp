package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.EngineTypeEnum;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferDetailsDTO {

    @NotEmpty(message = "{add.offer.description.length}")
    @Size(message = "{add.offer.description.length}", min = 50, max = 50)
    private String description;

    @NotEmpty
    @PositiveOrZero
    private int mileage;

    @NotEmpty
    @PositiveOrZero
    private int price;

    @NotEmpty
    private EngineTypeEnum category;
}
