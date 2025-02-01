package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UserRegistrationDTO {

    @NotEmpty
    @Size(min = 5, max = 20)
    private String firstName;

    @NotEmpty
    @Size(min = 5, max = 20)
    private String lastName;

    @NotEmpty
    private String password;

    @NotEmpty
    @Email
    private String email;

    @Override
    public String toString() {
        return "UserRegistrationDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + (password == null ? "N/A" : "[PROVIDED]") + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
