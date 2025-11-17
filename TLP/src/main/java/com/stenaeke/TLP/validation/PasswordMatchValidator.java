package com.stenaeke.TLP.validation;

import com.stenaeke.TLP.dtos.teacher.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public boolean isValid(Object user, ConstraintValidatorContext constraintValidatorContext) {
        RegisterRequest request = (RegisterRequest) user;
        return request.getPassword().equals(request.getConfirmPassword());
    }
}
