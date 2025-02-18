package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.model.entity.UserEntity;
import spring.app.Mobile.service.interfaces.JwtService;
import spring.app.Mobile.service.interfaces.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/verify-email")
    public String showVerification(@RequestParam(value = "token", required = false) String token,
                                   Model model) {
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
                return "redirect:/users/verify-email";
            }

        } catch (Exception e) {
            rAtt.addFlashAttribute("error", "An unexpected error occurred! Please try again");
            System.err.println("Unexpected error: " + e.getMessage());
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
            try {
                userService.sendPasswordResetEmail(userEntity);
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
