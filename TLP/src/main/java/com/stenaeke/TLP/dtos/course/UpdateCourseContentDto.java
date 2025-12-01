package com.stenaeke.TLP.dtos.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public final class UpdateCourseContentDto implements UpdateCourseDto {
    @NotBlank
    private String description;

}
