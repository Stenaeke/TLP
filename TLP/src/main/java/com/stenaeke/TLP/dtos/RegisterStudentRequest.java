package com.stenaeke.TLP.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterStudentRequest {

    @NotBlank(message = "First name can't be empty")
    @Size(max = 255, message = "First name must be less than 255 characters")
    private String firstName;

    @NotBlank(message = "Last name can't be empty")
    @Size(max = 255, message = "Last name must be less than 255 characters")
    private String lastName;

    @NotBlank(message = "Email name can't be empty")
    private String email;

    @NotBlank(message = "Password name can't be empty")
    @Size(min = 6, max = 25, message = "Password must be between 6 and 2 characters")
    private String password;
    //TODO:implement passowrd equal constraint https://stackoverflow.com/questions/7239897/spring-3-annotation-based-validation-password-and-confirm-password
    private String confirmPassword;

}
