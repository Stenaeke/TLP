package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherAuthService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public Teacher authenticate(String email, String inputPassword) {
        var teacher = teacherRepository.findByEmail(email).orElseThrow(()
                -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(inputPassword, teacher.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return teacher;
    }
}
