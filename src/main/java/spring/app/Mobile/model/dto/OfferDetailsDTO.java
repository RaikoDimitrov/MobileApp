package spring.app.Mobile.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class OfferDetailsDTO extends OfferBaseDTO {

    private VehicleTypeEnum category;
    private EngineTypeEnum engine;
    private TransmissionTypeEnum transmission;
    private ChassisTypeEnum chassis;

}
