package spring.app.Mobile.model.user;

import lombok.Getter;
import lombok.Setter;
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

    public UserMobileDetails(UUID uuid,
                             String username,
                             String email,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String firstName,
                             String lastName) {
        super(username, password, authorities);
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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
