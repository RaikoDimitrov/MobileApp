package spring.app.Mobile.service.interfaces;

import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;

public interface JwtService {
    String generateToken(Map<String, Object> claims);

    Claims extractClaims(String token);

    String generatePasswordResetToken(String email);

    String generateEmailVerificationToken(String email);

    boolean validateEmailVerificationToken(String token);

    boolean validatePasswordResetToken(String token);

    String generateBearerToken(String uuid, Map<String, List<String>> roles);
}
