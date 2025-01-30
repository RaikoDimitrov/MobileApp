package spring.app.Mobile.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {

    private static final String ANONYMOUS = "anonymous";
    private String name = ANONYMOUS;
    private boolean isAnonymous;

    public CurrentUser setAnonymous(boolean anonymous) {
        if (anonymous) this.name = ANONYMOUS;
        isAnonymous = anonymous;
        return this;
    }
}
