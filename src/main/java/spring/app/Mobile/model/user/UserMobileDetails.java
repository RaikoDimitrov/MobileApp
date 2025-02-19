package spring.app.Mobile.model.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserMobileDetails extends User {

    private final UUID uuid;

    private final String email;

    private final String firstName;

    private final String lastName;

    private final boolean isVerified;

    public UserMobileDetails(UUID uuid,
                             String username,
                             String email,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String firstName,
                             String lastName, boolean isVerified) {
        super(username, password, authorities);
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isVerified = isVerified;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) {
            fullName.append(firstName);
        }
        if (lastName != null) {
            if (!fullName.isEmpty()) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }
        return fullName.toString();
    }
}
