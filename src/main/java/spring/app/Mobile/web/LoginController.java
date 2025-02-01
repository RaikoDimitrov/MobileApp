package spring.app.Mobile.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.app.Mobile.model.dto.UserLoginDTO;
import spring.app.Mobile.service.interfaces.UserService;

@Controller
@RequestMapping("/users")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "auth-login";
    }

    @PostMapping("/login")
    public String login(UserLoginDTO model) {
        if (userService.authenticate(model.username(), model.password())) {
            userService.loginUser(model.username());
            return "redirect:/";
        } else {
            return "redirect:/users/login";
        }
    }
}
