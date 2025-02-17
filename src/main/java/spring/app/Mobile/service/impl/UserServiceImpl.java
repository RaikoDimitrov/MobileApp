package spring.app.Mobile.service.impl;

import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.app.Mobile.config.AppProperties;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.BaseEntity;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.model.entity.UserRoleEntity;
import spring.app.Mobile.model.enums.UserRoleEnum;
import spring.app.Mobile.model.user.UserMobileDetails;
import spring.app.Mobile.repository.UserRepository;
import spring.app.Mobile.repository.UserRoleRepository;
import spring.app.Mobile.security.CurrentUser;
import spring.app.Mobile.service.interfaces.EmailService;
import spring.app.Mobile.service.interfaces.JwtService;
import spring.app.Mobile.service.interfaces.UserService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableConfigurationProperties(AppProperties.class)
public class UserServiceImpl implements UserService {

    private final AppProperties appProperties;

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUser currentUser;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserMobileDetailsServiceImpl userMobileDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void resetAutoIncrement() {
        jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
    }

    @Autowired
    public UserServiceImpl(AppProperties appProperties,
                           ModelMapper modelMapper,
                           UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder, CurrentUser currentUser,
                           JwtService jwtService,
                           EmailService emailService,
                           AuthenticationManager authenticationManager, UserMobileDetailsServiceImpl userMobileDetailsService) {
        this.appProperties = appProperties;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUser = currentUser;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.userMobileDetailsService = userMobileDetailsService;
    }


    @Override
    public void registerUser(UserRegistrationDTO userRegistrationDTO) {

        UserEntity userEntity = userRepository.save(map(userRegistrationDTO));
        sendVerificationEmail(userEntity);
    }

    @Override
    public void authenticateAfterVerification(String email, HttpServletRequest request) {
        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found."));


        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword(), userEntity.getAuthorities());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            System.err.println("Bad credentials: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        //store security context in the session if needed
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }

    }

    private void sendVerificationEmail(UserEntity userEntity) {
        String subject = "Email verification";
        String verificationToken = jwtService.generateEmailVerificationToken(userEntity.getEmail());
        String verificationLink = appProperties.getBaseUrl() + "/verify-email?token=" + URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);
        String message = "Click the link below to verify your email:\n" + verificationLink
                + "\nOr enter the following code manually: " + verificationToken;

        try {
            emailService.sendEmail(userEntity.getEmail(), subject, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email.", e);
        }
    }

    @Transactional
    @Override
    public boolean verifyEmail(String token) {
        if (!jwtService.validateEmailVerificationToken(token)) {
            return false;
        }
        Claims claims = jwtService.extractClaims(token);
        String email = claims.get("email", String.class);
        Optional<UserEntity> optionalUser = userRepository.findByEmailIgnoreCase(email);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            if (userEntity.isVerified()) {
                return true;
            }
            userEntity.setVerified(true);
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean passwordReset(String token, String newPassword, String confirmPassword) {
        if (!jwtService.validatePasswordResetToken(token)) {
            return false;
        }
        if (newPassword.length() < 8) throw new RuntimeException("Password must be at least 8 symbols");
        if (!newPassword.equals(confirmPassword)) throw new RuntimeException("Passwords do not match");

        Claims claims = jwtService.extractClaims(token);
        String email = claims.get("email", String.class);
        Optional<UserEntity> optionalUser = userRepository.findByEmailIgnoreCase(email);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public void sendPasswordResetEmail(UserEntity userEntity) {
        String subject = "Reset your password!";
        String passwordResetToken = jwtService.generatePasswordResetToken(userEntity.getEmail());
        String passwordResetLink = appProperties.getBaseUrl()
                + "/users/reset-password?token="
                + URLEncoder.encode(passwordResetToken, StandardCharsets.UTF_8);
        String text = "Click the link to reset your password: " + passwordResetLink;

        try {
            emailService.sendEmail(userEntity.getEmail(), subject, text);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email.", e);
        }

    }

    @Override
    public Optional<UserMobileDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserMobileDetails userMobileDetails) {
            return Optional.of(userMobileDetails);
        }
        return Optional.empty();
    }

    @Override
    public void loginUser(String username) {
        currentUser.setUsername(username);
        currentUser.setAuthenticated(username);
    }

    @Override
    @Transactional
    public List<UserEntity> initializeUsers() {

        List<UserEntity> users = new ArrayList<>();
        logger.info("Users count in DB: " + userRepository.count());
        if (userRepository.count() == 0) {
            users.add(UserEntity.builder()
                    .firstName("Raiko")
                    .lastName("Dimitrov")
                    .username("freddy")
                    .email("freddy98@abv.bg")
                    .isVerified(true)
                    .roles(new ArrayList<>(List.of(UserRoleEntity.builder().role(UserRoleEnum.ADMIN).build())))
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

    //private methods
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
        setCurrentTimeStamps(mappedEntity);
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
    }
}
