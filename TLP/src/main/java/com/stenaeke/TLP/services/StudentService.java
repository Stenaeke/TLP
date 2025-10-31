package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Student;
import com.stenaeke.TLP.dtos.RegisterRequest;
import com.stenaeke.TLP.dtos.StudentDTO;
import com.stenaeke.TLP.dtos.UpdateUserRequest;
import com.stenaeke.TLP.mappers.StudentMapper;
import com.stenaeke.TLP.repositories.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public Iterable<StudentDTO> getAllStudentsAsDTO() {
        return studentRepository.findAll().stream().map(studentMapper::mapToDTO).toList();
    }

    public StudentDTO registerStudent(RegisterRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Student registeredStudent = new Student();
        registeredStudent.setFirstName(registerRequest.getFirstName());
        registeredStudent.setLastName(registerRequest.getLastName());
        registeredStudent.setEmail(registerRequest.getEmail());
        registeredStudent.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        studentRepository.save(registeredStudent);

        return studentMapper.mapToDTO(registeredStudent);
    }

    public StudentDTO getStudentById(Long id) {
        var student = studentRepository.findById(id).orElse(null);
        return studentMapper.mapToDTO(student);
    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    public StudentDTO updateStudent(Long id, UpdateUserRequest updateRequest) {
        var student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            return null;
        }

        if (updateRequest.getFirstName() != null) {
            student.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            student.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            student.setEmail(updateRequest.getEmail());
        }

        studentRepository.save(student);
        return studentMapper.mapToDTO(student);
    }

}
