package spring.app.Mobile.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.app.Mobile.model.dto.OfferAddDTO;
import spring.app.Mobile.model.dto.OfferDetailsDTO;
import spring.app.Mobile.model.dto.OfferSummaryDTO;
import spring.app.Mobile.model.entity.OfferEntity;

@Configuration
public class OfferMapperConfig {

    @Bean
    public ModelMapper offerMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<OfferAddDTO, OfferEntity> typeMap = modelMapper.createTypeMap(OfferAddDTO.class, OfferEntity.class);
        typeMap.addMappings(mapper -> {
            mapper.skip(OfferEntity::setId);
        });

        modelMapper.typeMap(OfferEntity.class, OfferSummaryDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getBrandEntity().getName(), OfferSummaryDTO::setBrandName);
            mapper.map(src -> src.getModelEntity().getName(), OfferSummaryDTO::setModelName);
        });

        modelMapper.typeMap(OfferEntity.class, OfferDetailsDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getBrandEntity().getName(), OfferDetailsDTO::setBrandName);
            mapper.map(src -> src.getModelEntity().getName(), OfferDetailsDTO::setModelName);
            mapper.map(src -> src.getSellerEntity().getUsername(), OfferDetailsDTO::setSellerUsername);
        });
        return modelMapper;
    }
}
