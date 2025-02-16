package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.service.interfaces.JwtService;
import spring.app.Mobile.service.interfaces.UserService;

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
                rAtt.addFlashAttribute("error", "Invalid verification code! Please try again.");
                return "redirect:/users/verify-email";
            }
        } catch (Exception e) {
            rAtt.addFlashAttribute("error", "An error occurred! Please try again.");
            return "redirect:/users/verify-email";
        }
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword) {
        try {
            boolean passwordReset = userService.passwordReset(token, newPassword);
            if (passwordReset) return "password-reset-success";
            else return "invalid-token";
        } catch (Exception e) {
            return "error";
        }
    }
}
