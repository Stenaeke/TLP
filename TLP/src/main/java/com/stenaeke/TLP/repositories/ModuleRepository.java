package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findBySubcategoryId(Long subcategoryId);
}
