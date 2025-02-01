package spring.app.Mobile.web;

import org.springframework.stereotype.Controller;
import spring.app.Mobile.service.interfaces.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }
}
