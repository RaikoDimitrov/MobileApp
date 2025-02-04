package spring.app.Mobile.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.user.UserMobileDetails;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.security.CurrentUser;
import spring.app.Mobile.service.interfaces.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) return false;
        else {
            if (passwordEncoder.matches(password, optionalUser.get().getPassword())) {
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
    }

    @Override
    public List<UserEntity> initializeUsers() {
        List<UserEntity> users = new ArrayList<>();
        if (userRepository.count() == 0) {
            users.add(UserEntity.builder()
                    .firstName("Raiko")
                    .lastName("Dimitrov")
                    .username("freddy")
                    .email("freddy98@abv.bg")
                    .password(passwordEncoder.encode("123123"))
                    .build());
            users.add(UserEntity.builder()
                    .firstName("Ivan")
                    .lastName("Petrov")
                    .username("ivan123")
                    .email("ivan@abv.bg")
                    .password(passwordEncoder.encode("123321"))
                    .build());
            users.forEach(this::setCurrentTimeStamps);
        }
        return userRepository.saveAll(users);
    }

    @Override
    public void registerUser(UserRegistrationDTO userRegistrationDTO) {
        userRepository.save(map(userRegistrationDTO));

    }

    @Override
    public Optional<UserMobileDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserMobileDetails userMobileDetails) {
            return Optional.of(userMobileDetails);
        }
        return Optional.empty();
    }

    private UserEntity map(UserRegistrationDTO userRegistrationDTO) {
        UserEntity mappedEntity = modelMapper.map(userRegistrationDTO, UserEntity.class);
        mappedEntity.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        mappedEntity.setCreated(Instant.now());
        mappedEntity.setUpdated(Instant.now());
        return mappedEntity;
    }
    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }
}
