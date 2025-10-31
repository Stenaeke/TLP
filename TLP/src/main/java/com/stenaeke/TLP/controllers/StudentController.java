package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.RegisterRequest;
import com.stenaeke.TLP.dtos.StudentDTO;
import com.stenaeke.TLP.dtos.UpdateUserRequest;
import com.stenaeke.TLP.services.StudentService;
import jakarta.validation.Valid;
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
    public Iterable<StudentDTO> getAllStudents() {
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

    @PostMapping("/register")
    public ResponseEntity<StudentDTO> createStudent(
            @Valid @RequestBody RegisterRequest registerRequest,
            UriComponentsBuilder uriComponentsBuilder
    ) {

        var studentDTO = studentService.registerStudent(registerRequest);

        var uri = uriComponentsBuilder.path("/student/{id}").buildAndExpand(studentDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(studentDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@Valid
                                                    @RequestBody UpdateUserRequest updateRequest,
                                                    @PathVariable Long id) {
        var updatedStudentDTO = studentService.updateStudent(id, updateRequest);
        if (updatedStudentDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedStudentDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.ok().build();
    }

}
