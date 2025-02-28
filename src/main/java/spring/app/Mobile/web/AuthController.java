package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.service.interfaces.JwtService;
import spring.app.Mobile.service.interfaces.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private static final Logger authLogger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    //register
    @ModelAttribute("registerDTO")
    public UserRegistrationDTO registrationDTO() {
        return new UserRegistrationDTO();
    }

    @GetMapping("/register")
    public String showRegister() {
        return "auth-register";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDTO") @Valid UserRegistrationDTO userDTO,
                               BindingResult result,
                               RedirectAttributes rAtt,
                               HttpServletRequest request) {
        if (result.hasErrors()) return "auth-register";
        userService.registerUser(userDTO, request);
        rAtt.addFlashAttribute("successMessage", "Registration successful! Please check your email.");
        return "redirect:/users/verify-email";
    }

    //login
    @GetMapping("/login")
    public String showLogin() {
        return "auth-login";
    }

    @GetMapping("/login-success")
    public String handleSuccessfulLogin(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Logged in successfully!");
        return "redirect:/";
    }

    @GetMapping("/login-error")
    public String showLoginErrorPage(HttpServletRequest request, Model model) {
        Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (exception instanceof BadCredentialsException) {
            model.addAttribute("error", "Invalid username or password!");
        } else if (exception instanceof DisabledException) {
            model.addAttribute("error", "Your email is not verified. Please check your email");
        } else if (exception != null) {
            model.addAttribute("error", exception.getMessage());
        }
        request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        return "auth-login";
    }

    //auth
    @GetMapping("/verify-email")
    public String showVerification(@RequestParam(value = "verificationCode", required = false) String token,
                                   @RequestParam(value = "resend", required = false) boolean resend,
                                   Model model,
                                   HttpServletRequest request) {
        if (resend) {
            String email = (String) request.getSession().getAttribute("email");
            if (email != null) {
                try {
                    userService.sendVerificationEmail(email);
                    model.addAttribute("successMessage", "New verification code has been sent!");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    model.addAttribute("error", "There was error resending the verification code");
                    return "email-verification";
                }
            }
        }


        if (token == null) {
            return "email-verification";
        }
        if (!jwtService.validateEmailVerificationToken(token)) {
            model.addAttribute("error", "Invalid or expired token");
            return "email-verification";
        }

        model.addAttribute("verificationCode", token);
        return "email-verification";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("verificationCode") String token,
                              HttpServletRequest request,
                              RedirectAttributes rAtt) {
        token = token.trim();
        try {
            boolean isVerified = userService.verifyEmail(token);
            if (isVerified) {
                String email = jwtService.extractClaims(token).get("email", String.class);
                userService.authenticateAfterVerification(email, request);
                rAtt.addFlashAttribute("successMessage", "Email verified successfully!");
                return "redirect:/users/login";
            } else {
                rAtt.addFlashAttribute("error", "Invalid verification code! Please try again");
                rAtt.addFlashAttribute("verificationCode", token);
                return "redirect:/users/verify-email";
            }

        } catch (Exception e) {
            rAtt.addFlashAttribute("error", "An unexpected error occurred! Please try again");
            System.err.println("Unexpected error: " + e.getMessage());
            rAtt.addFlashAttribute("verificationCode", token);
            return "redirect:/users/verify-email";
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendPasswordLink(@RequestParam(value = "email") String email, Model model) {
        Optional<UserEntity> optionalUserEntity = userService.findByEmail(email);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            String emailEntity = userEntity.getEmail();
            try {
                userService.sendPasswordResetEmail(emailEntity);
                model.addAttribute("successMessage", "Password reset link has been sent to your email");

            } catch (Exception e) {
                model.addAttribute("error", "Failed to send password reset email. Please try again");
            }
            return "forgot-password";
        } else {
            model.addAttribute("error", "Email address not found");
            return "forgot-password";
        }
    }


    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(value = "token", required = false) String token,
                                    Model model) {
        if (token == null) {
            token = (String) model.asMap().get("token");
        }

        if (token == null || !jwtService.validatePasswordResetToken(token)) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";

    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes rAtt) {
        token = token.trim();

        if (newPassword.length() < 5) {
            rAtt.addFlashAttribute("error", "Password must be at least 5 symbols");
            rAtt.addFlashAttribute("token", token);
            return "redirect:/users/reset-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            rAtt.addFlashAttribute("error", "Passwords do not match");
            rAtt.addFlashAttribute("token", token);
            return "redirect:/users/reset-password";
        }

        try {
            boolean passwordReset = userService.passwordReset(token, newPassword, confirmPassword);
            if (passwordReset) {
                rAtt.addFlashAttribute("successMessage", "Your password has been successfully reset");
                return "redirect:/users/login";
            } else {
                rAtt.addFlashAttribute("error", "An unexpected error occurred! Please try again");
                rAtt.addFlashAttribute("token", token);
                return "redirect:/users/reset-password";
            }
        } catch (Exception e) {
            rAtt.addFlashAttribute("error", "An unexpected error occurred! Please try again");
            System.err.println("Unexpected error: " + e.getMessage());
            rAtt.addFlashAttribute("token", token);
            return "redirect:/users/reset-password";
        }
    }

}
