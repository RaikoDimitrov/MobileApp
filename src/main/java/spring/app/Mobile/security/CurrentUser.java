package spring.app.Mobile.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Getter
@Setter
public class CurrentUser {

    private static final String GUEST = "Guest";
    private String username = GUEST;
    private boolean isGuest = true;

    public CurrentUser setGuest() {
        this.username = GUEST;
        this.isGuest = true;
        return this;
    }

    public CurrentUser setAuthenticated(String username) {
        if (username != null & !username.isEmpty()) {
            this.username = username;
            this.isGuest = false;
        } else setGuest();
        return this;
    }
}
