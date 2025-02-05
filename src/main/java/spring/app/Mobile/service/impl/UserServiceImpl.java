package spring.app.Mobile.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.entity.UserRoleEntity;
import spring.app.Mobile.model.enums.UserRoleEnum;
import spring.app.Mobile.model.user.UserMobileDetails;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.repository.UserRoleRepository;
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
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, CurrentUser currentUser, AuthenticationManager authenticationManager) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
        this.authenticationManager = authenticationManager;
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
    @Transactional
    public List<UserEntity> initializeUsers() {
        logger.info("Initializing users: ");
        userRepository.deleteAll();
        List<UserEntity> users = new ArrayList<>();
        logger.info("Users count in DB: " + userRepository.count());
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
            users.forEach(user -> {
                setCurrentTimeStamps(user);
                setDefaultRole(user);
            });
            userRepository.saveAll(users);
        }
        return users;
    }

    @Override
    public void registerUser(UserRegistrationDTO userRegistrationDTO) {
        userRepository.save(map(userRegistrationDTO));

        //todo: login user after successful registration
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userRegistrationDTO.getUsername(), userRegistrationDTO.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Manually trigger the authentication success event
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
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
        UserRoleEntity defaultRole = userRoleRepository.findByRole(UserRoleEnum.USER)
                .orElseGet(() -> {
                    UserRoleEntity newRole = new UserRoleEntity();
                    newRole.setRole(UserRoleEnum.USER);
                    userRoleRepository.save(newRole);
                    return newRole;
                });
        UserEntity mappedEntity = modelMapper.map(userRegistrationDTO, UserEntity.class);
        mappedEntity.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        mappedEntity.setCreated(Instant.now());
        mappedEntity.setUpdated(Instant.now());
        if (!mappedEntity.getRoles().contains(defaultRole)) {
            mappedEntity.getRoles().add(defaultRole);
        }
        return mappedEntity;
    }

    private void setCurrentTimeStamps(BaseEntity baseEntity) {
        baseEntity.setCreated(Instant.now());
        baseEntity.setUpdated(Instant.now());
    }

    private void setDefaultRole(UserEntity userEntity) {
        logger.info("Setting role for user: " + userEntity.getUsername());
        if (userEntity.getRoles() == null) userEntity.setRoles(new ArrayList<>());
        UserRoleEntity defaultRole = userRoleRepository.findByRole(UserRoleEnum.USER)
                .orElseGet(() -> {
                    UserRoleEntity newRole = new UserRoleEntity();
                    newRole.setRole(UserRoleEnum.USER);
                    userRoleRepository.save(newRole);
                    return newRole;
                });
        if (!userEntity.getRoles().contains(defaultRole)) {
            userEntity.getRoles().add(defaultRole);
        }
        logger.info("User ROLE before saving: " + userEntity.getRoles());
    }
}
