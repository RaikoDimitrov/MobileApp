package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

@Getter
@Setter
public class OfferCreateDTO {

    @NotEmpty(message = "Description cannot be empty.")
    @Size(message = "Description must be between 50-500 symbols", min = 50, max = 500)
    private String description;

    @PositiveOrZero(message = "Mileage must be positive number.")
    private Integer mileage;

    @Positive(message = "Price must be positive number.")
    private int price;

    @NotNull(message = "Category is required.")
    private VehicleTypeEnum category;

    @NotNull(message = "Engine is required.")
    private EngineTypeEnum engine;

    @NotNull(message = "Transmission is required.")
    private TransmissionTypeEnum transmission;

    @NotNull(message = "Chassis is required.")
    private ChassisTypeEnum chassis;

    @NotEmpty(message = "Brand name is required.")
    private String brandName;

    @NotEmpty(message = "Model name is required.")
    private String modelName;
}
