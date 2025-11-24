package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
