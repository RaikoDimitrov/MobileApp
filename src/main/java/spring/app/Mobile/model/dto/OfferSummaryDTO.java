package spring.app.Mobile.model.dto;

import spring.app.Mobile.model.enums.EngineTypeEnum;

public record OfferSummaryDTO(Long id,
                              String description,
                              int mileage,
                              int price,
                              EngineTypeEnum category) {
}
