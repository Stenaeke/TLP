package com.stenaeke.TLP.dtos.module;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateModuleRequest {
    @NotEmpty
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    private String description;
    @NotNull
    private Boolean published;
    private String content;
    @NotNull
    private long subcategoryId;
}

