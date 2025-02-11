package spring.app.Mobile.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import spring.app.Mobile.service.impl.UserMobileDetailsServiceImpl;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    private final UserMobileDetailsServiceImpl userMobileDetailsService;

    public SecurityConfig(UserMobileDetailsServiceImpl userMobileDetailsService) {
        this.userMobileDetailsService = userMobileDetailsService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.
                csrf(csrf -> csrf.ignoringRequestMatchers("/logout")).
                authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/favicon.ico", "/", "/users/login", "/logout", "/users/register",
                                        "/error", "/offers/all", "/offers/{id}", "/api/convert").permitAll()
                                .requestMatchers("/offers/add").authenticated()
                                .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(((request, response, authException) ->
                        response.sendRedirect("/users/login"))))
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/users/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/users/login-error")
                                .permitAll()
                                .successHandler(authenticationSuccessHandler(userMobileDetailsService)))
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true))
                .userDetailsService(userMobileDetailsService)
                .build();
    }

    // Custom AuthenticationSuccessHandler to update currentUser
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(UserMobileDetailsServiceImpl userMobileDetailsService) {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                System.out.println("Authenticated user from handler: " + authentication.getName());
                userMobileDetailsService.handlePostLogin(authentication);
                response.sendRedirect("/");
            }
        };
    }

    //manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }

    //encoding password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
