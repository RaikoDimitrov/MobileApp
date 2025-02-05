package spring.app.Mobile.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import spring.app.Mobile.validation.PasswordMatch;

@SuperBuilder
@Getter
@Setter
@PasswordMatch
public class UserRegistrationDTO {
    public UserRegistrationDTO() {
    }

    @NotBlank(message = "Username is required.")
    @Size(min = 5, max = 20, message = "Username must be between 5 - 20 symbols.")
    private String username;
    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, message = "Password must be at least 5 symbols.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;

    @NotBlank(message = "Email is required.")
    @Email
    private String email;

    @Override
    public String toString() {
        return "UserRegistrationDTO{" +
                "username='" + username + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + (password == null ? "N/A" : "[PROVIDED]") + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
