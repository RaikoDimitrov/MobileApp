package spring.app.Mobile.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserMobileDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void sendVerificationEmail(String email);
    void sendPasswordResetEmail(String email);

    void authenticateAfterVerification(String email, HttpServletRequest request);

    void registerUser(UserRegistrationDTO userRegistrationDTO, HttpServletRequest request);

    Optional<UserMobileDetails> getCurrentUser();

    List<UserEntity> initializeUsers();

    boolean verifyEmail(String token);

    boolean passwordReset(String token, String newPassword, String confirmPassword);

    Optional<UserEntity> findByEmail(String email);
}
