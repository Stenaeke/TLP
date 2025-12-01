package com.stenaeke.TLP.dtos.module;

import lombok.Data;

@Data
public final class UpdateModuleContentDto implements UpdateModuleDto {
    private String content;
}
