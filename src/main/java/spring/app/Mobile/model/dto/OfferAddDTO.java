package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

import java.time.Year;

@Data
@NoArgsConstructor
public class OfferAddDTO extends OfferBaseDTO {

    @NotEmpty(message = "Description cannot be empty.")
    @Size(message = "Description must be between 50-500 symbols", min = 50, max = 500)
    private String description;

    @PositiveOrZero(message = "Mileage must be positive number.")
    private Integer mileage;

    @Positive(message = "Price must be positive number.")
    private Integer price;

    @Min(value = 1900, message = "Year must be 1900 or older.")
    @Max(value = Year.MAX_VALUE, message = "Year cannot be in the future.")
    private Integer year;

    @NotNull(message = "Category is required.")
    private VehicleTypeEnum vehicleType;

    @NotNull(message = "Engine is required.")
    private EngineTypeEnum engineType;

    @NotNull(message = "Transmission is required.")
    private TransmissionTypeEnum transmissionType;

    @NotNull(message = "Chassis is required.")
    private ChassisTypeEnum chassisType;

    @NotEmpty(message = "Brand name is required.")
    private String brandName;

    @NotEmpty(message = "Model name is required.")
    private String modelName;

    @NotEmpty(message = "Image is required.")
    private String imageUrl;

    public static OfferAddDTO empty() {
        return new OfferAddDTO();
    }
}
