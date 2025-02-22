package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BrandEntity extends BaseEntity {

    @Column(name = "brand_name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brandEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ModelEntity> modelsEntity = new ArrayList<>();

    public void setModelsEntity(List<ModelEntity> modelsEntity) {
        this.modelsEntity = modelsEntity;
        for (ModelEntity modelEntity : modelsEntity) {
            modelEntity.setBrandEntity(this);
        }
    }
}
