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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
                               RedirectAttributes rAtt) {
        if (result.hasErrors()) return "auth-register";
        userService.registerUser(userDTO);
        rAtt.addFlashAttribute("successMessage", "Registration successful!");
        return "redirect:/users/verify-email";
    }

}
