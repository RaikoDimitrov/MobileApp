package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.app.Mobile.service.interfaces.UserService;

@Controller
@RequestMapping("/users")
public class LoginController {

    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(LoginController.class);


    public LoginController(UserService userService) {
        this.userService = userService;
    }

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

}
