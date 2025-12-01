package com.stenaeke.TLP.dtos.module;

import com.stenaeke.TLP.domain.Module;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class UpdateModuleTitleDto implements UpdateModuleDto {

    @NotEmpty
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

}
