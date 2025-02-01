package spring.app.Mobile.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    //mapper
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //sql initializer
}
