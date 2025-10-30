package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.RegisterStudentRequest;
import com.stenaeke.TLP.dtos.StudentDTO;
import com.stenaeke.TLP.services.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Iterable<StudentDTO> getStudents() {
        return studentService.getAllStudentsAsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        var studentDTO = studentService.getStudentById(id);

        if (studentDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(studentDTO);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(
            @RequestBody RegisterStudentRequest registerRequest,
            UriComponentsBuilder uriComponentsBuilder
    ) {

        var studentDTO = studentService.registerStudent(registerRequest);

        var uri = uriComponentsBuilder.path("/student/{id}").buildAndExpand(studentDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(studentDTO);
    }
}
