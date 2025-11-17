package com.stenaeke.TLP.bootstrap;

import com.stenaeke.TLP.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserBootstrap implements CommandLineRunner {

    private final TeacherRepository teacherRepository;

    @Override
    public void run(String... args) throws Exception {

//
//        Teacher teacher1 = Teacher.builder()
//                .firstName("test")
//                .lastName("teacher")
//                .email("test6@test.com")
//                .passwordHash("123123213")
//                .build();
//        teacherRepository.save(teacher1);
    }
}
