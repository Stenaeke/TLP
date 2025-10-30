package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
