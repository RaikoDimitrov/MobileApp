package spring.app.Mobile.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BrandEntity extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "brandEntity", cascade = CascadeType.ALL)
    private List<ModelEntity> modelsEntity;

    public void setModelsEntity(List<ModelEntity> modelsEntity) {
        this.modelsEntity = modelsEntity;
    }
}
