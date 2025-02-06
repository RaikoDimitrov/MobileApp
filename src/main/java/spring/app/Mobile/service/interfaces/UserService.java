package spring.app.Mobile.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserMobileDetails;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean authenticate(String username, String password);

    void registerUser(UserRegistrationDTO userRegistrationDTO, HttpServletRequest request, HttpServletResponse response);

    Optional<UserMobileDetails> getCurrentUser();

    void loginUser(String username);

    List<UserEntity> initializeUsers();

}
