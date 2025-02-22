package spring.app.Mobile.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ModelEntity that = (ModelEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
