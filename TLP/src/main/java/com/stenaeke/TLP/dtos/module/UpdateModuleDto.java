package com.stenaeke.TLP.dtos.module;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateModuleDto {
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    private Boolean published;
    @Size(min = 3, message = "Content must be at least 3 characters")
    private String content;
    private Long subcategoryId;
}
