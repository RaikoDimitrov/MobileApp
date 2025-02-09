package spring.app.Mobile.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(OfferApiConfig.class)
@ConfigurationProperties(prefix = "offers.api")
@Getter
@Setter
public class OfferApiConfig {
    private String baseUrl;
}
