package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @GetMapping("/")
    public String successfulLogin(Model model, HttpServletRequest request) {
        //todo: fix message for error and success to pop up with redirectattr
        String successMessage = (String) request.getSession().getAttribute("successMessage");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            request.getSession().removeAttribute("successMessage");
        }
        return "index";
    }

    @GetMapping("/login-error")
    public String showLoginErrorPage(RedirectAttributes rAtt) {
        rAtt.addFlashAttribute("error", "Invalid username or password!");
        return "auth-login";
    }

}
