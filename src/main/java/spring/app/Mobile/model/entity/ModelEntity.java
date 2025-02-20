package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

@Entity
@Table(name = "models")
@Data
@SuperBuilder
@NoArgsConstructor
public class ModelEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column
    private String imageUrl;

    private Integer startYear;

    private Integer endYear;

    @ManyToOne
    @JoinColumn(name = "brand_entity_id", nullable = false)
    private BrandEntity brandEntity;

}
