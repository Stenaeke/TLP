package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
