package com.stenaeke.TLP.bootstrap;

import com.stenaeke.TLP.domain.Student;
import com.stenaeke.TLP.repositories.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class UserBootstrap implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public UserBootstrap(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) throws Exception {

//        Student student1 = Student.builder()
//                .firstName("John")
//                .lastName("Doe")
//                .email("test1@test.com")
//                .passwordHash("1234")
//                .build();
//
//        Student student2 = Student.builder()
//                .firstName("Jane")
//                .lastName("Doe")
//                .email("test2@test.com")
//                .passwordHash("12345")
//                .build();
//
//        Student student3 = Student.builder()
//                .firstName("Jack")
//                .lastName("Sparrow")
//                .email("test3@test.com")
//                .passwordHash("12346")
//                .build();
//
//        studentRepository.save(student1);
//        studentRepository.save(student2);
//        studentRepository.save(student3);
    }
}
