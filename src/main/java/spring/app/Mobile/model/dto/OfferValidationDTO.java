package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import spring.app.Mobile.model.enums.ChassisTypeEnum;
import spring.app.Mobile.model.enums.EngineTypeEnum;
import spring.app.Mobile.model.enums.TransmissionTypeEnum;
import spring.app.Mobile.model.enums.VehicleTypeEnum;

import java.time.Year;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class OfferValidationDTO {

    @NotBlank(message = "Description cannot be empty")
    @Size(message = "Description must be between 50-500 symbols", min = 50, max = 500)
    private String description;

    @PositiveOrZero(message = "Mileage must be positive number")
    @NotNull(message = "Mileage in kilometers is required")
    private Integer mileage;

    @Positive(message = "Price must be positive number")
    @NotNull(message = "Price is required")
    private Integer price;

    @Min(value = 1900, message = "Year must be 1900 or older")
    @NotNull(message = "Manufactured year is required")
    private Integer year;

    @AssertTrue(message = "Year cannot be in the future")
    private boolean isYearValid() {
        return year == null || year <= Year.now().getValue();
    }

    @Positive(message = "Horse Power must be positive number")
    @NotNull(message = "Horse power is required")
    private Integer horsePower;

    @NotNull(message = "Category is required")
    private VehicleTypeEnum vehicleType;

    @NotNull(message = "Engine is required")
    private EngineTypeEnum engineType;

    @NotNull(message = "Transmission is required")
    private TransmissionTypeEnum transmissionType;

    @NotNull(message = "Chassis is required")
    private ChassisTypeEnum chassisType;

    @NotBlank(message = "Brand is required")
    private String brandName;

    @NotEmpty(message = "Model is required")
    private String modelName;

    @NotEmpty(message = "Please upload at least one image")
    private List<MultipartFile> images;

    private Integer mainImageIndex;

}
