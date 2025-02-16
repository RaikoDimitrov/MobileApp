package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String verifyEmail(@RequestParam("token") String token, HttpServletRequest request) {

        try {
            boolean isVerified = userService.verifyEmail(token);
            if (isVerified) {
                String email = jwtService.extractClaims(token).get("email", String.class);
                userService.authenticateAfterVerification(email, request);
                return "redirect:/";
            } else return "invalid-token";
        } catch (Exception e) {
            return "error";
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
