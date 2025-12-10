package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findByCourseId(long courseId);
}
