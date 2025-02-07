package spring.app.Mobile.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.EngineTypeEnum;

@Entity
@Table(name = "offers")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class OfferEntity extends BaseEntity {


    @NotEmpty
    private String description;

    @Positive
    private Integer mileage;

    @Positive
    private int price;

    @Enumerated(EnumType.STRING)
    private EngineTypeEnum engine;

}
