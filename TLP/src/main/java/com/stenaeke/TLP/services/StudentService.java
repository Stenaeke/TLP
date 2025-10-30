package com.stenaeke.TLP.services;

import com.stenaeke.TLP.domain.Student;
import com.stenaeke.TLP.dtos.RegisterStudentRequest;
import com.stenaeke.TLP.dtos.StudentDTO;
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

    public StudentDTO registerStudent(RegisterStudentRequest registerStudentRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Student registeredStudent = new Student();
        registeredStudent.setFirstName(registerStudentRequest.getFirstName());
        registeredStudent.setLastName(registerStudentRequest.getLastName());
        registeredStudent.setEmail(registerStudentRequest.getEmail());
        registeredStudent.setPasswordHash(passwordEncoder.encode(registerStudentRequest.getPassword()));
        studentRepository.save(registeredStudent);

        return studentMapper.mapToDTO(registeredStudent);
    }

    public StudentDTO getStudentById(Long id) {
        return studentMapper.mapToDTO(studentRepository.findById(id).get());
    }

}
