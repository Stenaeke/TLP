package com.stenaeke.TLP.dtos.module;

import com.stenaeke.TLP.domain.Module;

public interface UpdateModuleDto {
    void applyToModule(Module module);
}
