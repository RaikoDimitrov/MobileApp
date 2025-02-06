package spring.app.Mobile.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import spring.app.Mobile.model.dto.UserRegistrationDTO;
import spring.app.Mobile.service.interfaces.UserService;

@Controller
@RequestMapping("/users")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

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
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (result.hasErrors()) return "auth-register";
        try {
            userService.registerUser(userDTO, request, response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Username is already taken!")) {
                result.rejectValue("username", "error.registerDTO", e.getMessage());
            } else if (e.getMessage().contains("Email is already taken!")) {
                result.rejectValue("email", "error.registerDTO", e.getMessage());
            }
            return "auth-register";
        }
        return "redirect:/";
    }

}
