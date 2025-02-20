package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

import java.time.Year;

@Data
@NoArgsConstructor
public class OfferSummaryDTO {

    private Long id;

    private String brandName;

    private String modelName;

    private String imageUrl;

    private Integer mileage;

    private Integer price;

    private Integer year;

    private Integer horsePower;






}
