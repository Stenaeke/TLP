package com.stenaeke.TLP.dtos.module;

import com.stenaeke.TLP.domain.Module;

public sealed interface UpdateModuleDto permits UpdateModuleDescriptionDto, UpdateModuleTitleDto {
}
