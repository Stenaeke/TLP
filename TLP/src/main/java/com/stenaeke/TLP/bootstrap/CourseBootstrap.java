package com.stenaeke.TLP.bootstrap;

import com.stenaeke.TLP.domain.Course;
import com.stenaeke.TLP.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CourseBootstrap implements CommandLineRunner {

    private final CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
//        Course course = new Course();
//        course.setTitle("Psychology");
//        course.setDescription("Psychology is the scientific study of the mind and behavior. " +
//                "It explores how people think, feel, and act both individually and in groups. " +
//                "Psychologists investigate mental processes such as perception, memory, emotion, " +
//                "and learning, and apply this knowledge to help individuals improve their well-being, " +
//                "solve problems, and understand themselves and others better. The field includes various " +
//                "branches like clinical psychology, cognitive psychology, developmental psychology, " +
//                "and social psychology.");
//
//        courseRepository.save(course);


    }


}
