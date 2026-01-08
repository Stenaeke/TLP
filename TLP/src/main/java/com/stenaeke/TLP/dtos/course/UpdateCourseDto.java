package com.stenaeke.TLP.dtos.course;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCourseDto {
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;
}
