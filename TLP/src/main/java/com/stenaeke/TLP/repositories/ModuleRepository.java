package com.stenaeke.TLP.repositories;

import com.stenaeke.TLP.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
