package com.stenaeke.TLP.dtos.teacher;

import com.stenaeke.TLP.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@PasswordMatch
public class RegisterRequest {

    @NotBlank(message = "First name can't be empty")
    @Size(min = 1, max = 255, message = "First name must be less than 255 characters")
    private String firstName;

    @NotBlank(message = "Last name can't be empty")
    @Size(min = 1, max = 255, message = "Last name must be less than 255 characters")
    private String lastName;

    @NotBlank(message = "Email name can't be empty")
    @Email
    private String email;

    @NotBlank(message = "Password name can't be empty")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 25 characters")
    private String password;

    private String confirmPassword;

}
