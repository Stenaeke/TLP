package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.auth.LoginRequest;
import com.stenaeke.TLP.dtos.auth.TokenResponse;
import com.stenaeke.TLP.services.JWTService;
import com.stenaeke.TLP.services.TeacherAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JWTService jwtService;
    private final TeacherAuthService teacherAuthService;


    @PostMapping("/teacher/login")
    public ResponseEntity<TokenResponse> loginTeacher(@RequestBody LoginRequest request) {

        Teacher teacher = teacherAuthService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(
                teacher.getEmail(),
                Map.of(
                        "role", "ROLE_TEACHER",
                        "id", teacher.getId(),
                        "firstName", teacher.getFirstName(),
                        "lastName", teacher.getLastName()
                    )
                );
                return ResponseEntity.ok(new TokenResponse(token));
    }
}