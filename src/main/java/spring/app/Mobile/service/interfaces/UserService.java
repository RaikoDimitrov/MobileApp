package spring.app.Mobile.service.interfaces;

import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserMobileDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean authenticate(String username, String password);

    void registerUser(UserRegistrationDTO userRegistrationDTO);

    Optional<UserMobileDetails> getCurrentUser();

    void loginUser(String username);

    List<UserEntity> initializeUsers();
}
