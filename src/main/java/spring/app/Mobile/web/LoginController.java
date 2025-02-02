package spring.app.Mobile.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.app.Mobile.service.interfaces.UserService;

@Controller
@RequestMapping("/users")
public class LoginController {


    @Autowired
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(LoginController.class);


    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "auth-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        logger.info("Login attempt for user: {}", username);
        if (userService.authenticate(username, password)) {
            userService.loginUser(username);
            return "redirect:/";
        } else {
            logger.info("Login failed for user: {}", username);
            model.addAttribute("error", "Invalid username or password");
            return "redirect:/users/login";
        }
    }

}
