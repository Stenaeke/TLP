package com.stenaeke.TLP.dtos.subcategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubcategoryDto {
    @NotEmpty
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    private String description;
    @NotNull
    private long courseId;
}
