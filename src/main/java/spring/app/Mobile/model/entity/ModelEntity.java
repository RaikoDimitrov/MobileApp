package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.EngineTypeEnum;

import java.time.Instant;

@Entity
@Table(name = "models")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ModelEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineTypeEnum category;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private Integer startYear;
    private Integer endYear;

    @ManyToOne
    private BrandEntity brandEntity;

}
