package spring.app.Mobile.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestClient;
import spring.app.Mobile.service.interfaces.JwtService;
import spring.app.Mobile.service.interfaces.UserService;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(OfferApiConfig.class)
public class RestConfig {

    @Bean("genericRestClient")
    public RestClient genericRestClient() {
        return RestClient.create();
    }


    @Bean("offerRestClient")
    public RestClient offerRestClient(OfferApiConfig offerApiConfig, ClientHttpRequestInterceptor requestInterceptor) {
        System.out.println("Base URL: " + offerApiConfig.getBaseUrl());
        return RestClient.builder()
                .baseUrl(offerApiConfig.getBaseUrl())
                .defaultHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor(requestInterceptor)
                .build();
    }

    @Bean
    public ClientHttpRequestInterceptor requestInterceptor(UserService userService, JwtService jwtService) {
        return ((request, body, execution) -> {
           userService.getCurrentUser()
                   .ifPresent(user -> {
                       String bearerToken = jwtService.generateBearerToken(
                               user.getUuid().toString(),
                               Map.of("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                       );
                       System.out.println("BEARER TOKEN: " + bearerToken);
                       request.getHeaders().setBearerAuth(bearerToken);
                   });
           return execution.execute(request, body);
        });
    }
}
