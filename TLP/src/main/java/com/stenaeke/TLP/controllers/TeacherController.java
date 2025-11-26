package com.stenaeke.TLP.controllers;

import com.stenaeke.TLP.dtos.teacher.RegisterRequest;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.dtos.teacher.UpdateTeacherRequest;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.services.TeacherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public Iterable<TeacherDto> getAllTeachers() {
        return teacherService.getAllTeachersAsDTO();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> getTeacher(@PathVariable Long id) {
        var teacherDTO = teacherService.getTeacherById(id);

        if (teacherDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(teacherDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<TeacherDto> createTeacher(
            @Valid @RequestBody RegisterRequest registerRequest,
            UriComponentsBuilder uriComponentsBuilder
    ) {

        var teacherDTO = teacherService.registerTeacher(registerRequest);

        var uri = uriComponentsBuilder.path("/teacher/{id}").buildAndExpand(teacherDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(teacherDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeacherDto> updateTeacher(@Valid
                                                    @RequestBody UpdateTeacherRequest updateRequest,
                                                    @PathVariable Long id) {
        var updatedTeacherDTO = teacherService.updateTeacher(id, updateRequest);
        return ResponseEntity.ok(updatedTeacherDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        if(teacherService.deleteTeacherById(id)) {
            return ResponseEntity.ok().build();
        }
        else  {
            return ResponseEntity.notFound().build();
        }
    }
}
