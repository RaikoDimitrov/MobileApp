package spring.app.Mobile.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDetailsDTO extends OfferBaseDTO {

    private VehicleTypeEnum vehicleType;
    private EngineTypeEnum engineType;
    private TransmissionTypeEnum transmissionType;
    private ChassisTypeEnum chassisType;

}
