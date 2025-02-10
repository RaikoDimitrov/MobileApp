package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

@Entity
@Table(name = "offers")
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferEntity extends BaseEntity {


    @NotEmpty
    private String description;

    @Positive
    @NotNull
    private Integer mileage;

    @Positive
    @NotNull
    private int price;

    @Enumerated(EnumType.STRING)
    private VehicleTypeEnum category;

    @Enumerated(EnumType.STRING)
    private EngineTypeEnum engine;

    @Enumerated(EnumType.STRING)
    private TransmissionTypeEnum transmission;

    @Enumerated(EnumType.STRING)
    private ChassisTypeEnum chassis;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brandEntity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelEntity modelEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserEntity sellerEntity;

    @NotEmpty
    private String imageUrl;


}
