package spring.app.Mobile.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/login-error")
    public String showLoginErrorPage(Model model) {
        model.addAttribute("error", "Invalid username or password!");
        return "auth-login";
    }

}
