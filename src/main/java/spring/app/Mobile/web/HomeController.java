package spring.app.Mobile.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import spring.app.Mobile.model.user.UserMobileDetails;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails instanceof UserMobileDetails user) {
            model.addAttribute("welcomeMessage", user.getFullName());
        } else {
            model.addAttribute("welcomeMessage", "Guest");
        }
        return "index";
    }
}
