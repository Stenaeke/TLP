package com.stenaeke.TLP.dtos.module;

import lombok.Data;

@Data
public final class UpdateModuleDescriptionDto implements UpdateModuleDto {
    private String description;
}
