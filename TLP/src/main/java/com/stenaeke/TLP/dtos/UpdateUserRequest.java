package com.stenaeke.TLP.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Size(min = 1, max = 255, message = "First name must be between 1 and 255 characters")
    String firstName;
    @Size(min = 1, max = 255, message = "Last name must be between 1 and 255 characters")
    String lastName;
    @Email
    String email;
}
