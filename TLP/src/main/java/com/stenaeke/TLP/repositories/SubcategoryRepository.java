package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
}
