package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
}
