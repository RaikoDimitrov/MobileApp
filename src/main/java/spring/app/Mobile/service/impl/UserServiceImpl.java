package spring.app.Mobile.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserDetails;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.security.CurrentUser;
import spring.app.Mobile.service.interfaces.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, CurrentUser currentUser) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
    }


    @Override
    public boolean authenticate(String username, String password) {
        System.out.println("entering authenticate");
        System.out.println("Attempting to authenticate user: " + username);
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) return false;
        else {
            if (passwordEncoder.matches(password, optionalUser.get().getPassword())) {
                currentUser.setUsername(optionalUser.get().getUsername());
                currentUser.setAuthenticated(optionalUser.get().getUsername());
                return true;
            } else {
                currentUser.setGuest();
                return false;
            }
        }
    }

    @Override
    public void loginUser(String username) {
        currentUser.setUsername(username);
        currentUser.setAuthenticated(username);
        System.out.println("User logged in: " + currentUser.getUsername());
    }

    @Override
    public void registerUser(UserRegistrationDTO userRegistrationDTO) {
        userRepository.save(map(userRegistrationDTO));

    }

    @Override
    public Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.of(userDetails);
        }
        return Optional.empty();
    }

    private UserEntity map(UserRegistrationDTO userRegistrationDTO) {
        UserEntity mappedEntity = modelMapper.map(userRegistrationDTO, UserEntity.class);
        mappedEntity.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        return mappedEntity;
    }
}
