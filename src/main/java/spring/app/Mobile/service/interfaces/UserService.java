package spring.app.Mobile.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserMobileDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void authenticateAfterVerification(String email, HttpServletRequest request);

    void registerUser(UserRegistrationDTO userRegistrationDTO);

    Optional<UserMobileDetails> getCurrentUser();

    void loginUser(String username);

    List<UserEntity> initializeUsers();

    boolean verifyEmail(String token);

    boolean passwordReset(String token, String newPassword);
}
