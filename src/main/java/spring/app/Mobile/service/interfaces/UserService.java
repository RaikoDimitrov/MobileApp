package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.user.UserDetails;

import java.util.Optional;

public interface UserService {

    boolean authenticate(String username, String password);

    void registerUser(UserRegistrationDTO userRegistrationDTO);

    Optional<UserDetails> getCurrentUser();

    void loginUser(String username);
}
