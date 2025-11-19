package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Teacher;
import com.stenaeke.TLP.dtos.teacher.RegisterRequest;
import com.stenaeke.TLP.dtos.teacher.TeacherDto;
import com.stenaeke.TLP.dtos.teacher.UpdateTeacherRequest;
import com.stenaeke.TLP.exceptions.ResourceNotFoundException;
import com.stenaeke.TLP.mappers.TeacherMapper;
import com.stenaeke.TLP.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Iterable<TeacherDto> getAllTeachersAsDTO() {
        return teacherRepository.findAll().stream().map(teacherMapper::mapToDTO).toList();
    }

    @Transactional
    public TeacherDto registerTeacher(RegisterRequest registerRequest) {
        Teacher registeredTeacher = new Teacher();
        registeredTeacher.setFirstName(registerRequest.getFirstName());
        registeredTeacher.setLastName(registerRequest.getLastName());
        registeredTeacher.setEmail(registerRequest.getEmail());
        registeredTeacher.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        teacherRepository.save(registeredTeacher);

        return teacherMapper.mapToDTO(registeredTeacher);
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherById(Long id) {
        var teacher = teacherRepository.findById(id).orElse(null);
        return teacherMapper.mapToDTO(teacher);
    }

    @Transactional
    public boolean deleteTeacherById(Long id) {
        var teacher = teacherRepository.findById(id).orElse(null);
        if (teacher != null) {
            teacherRepository.delete(teacher);
            return true;
        } else  {
            return false;
        }
    }

    @Transactional
    public TeacherDto updateTeacher(Long id, UpdateTeacherRequest updateRequest) {
        var teacher = teacherRepository.findById(id).orElse(null);

        if (teacher == null) {
            throw new ResourceNotFoundException("Teacher not found");
        }

        if (updateRequest.getFirstName() != null) {
            teacher.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            teacher.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            teacher.setEmail(updateRequest.getEmail());
        }

        teacherRepository.save(teacher);
        return teacherMapper.mapToDTO(teacher);
    }
}
