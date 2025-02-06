package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BrandEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brandEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ModelEntity> modelsEntity = new ArrayList<>();

    public void setModelsEntity(List<ModelEntity> modelsEntity) {
        this.modelsEntity = modelsEntity;
        for (ModelEntity modelEntity : modelsEntity) {
            modelEntity.setBrandEntity(this);
        }
    }
}
