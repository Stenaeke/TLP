package com.stenaeke.TLP.dtos.subcategory;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateSubcategoryDto {
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String description;
    private Long courseId;

}
