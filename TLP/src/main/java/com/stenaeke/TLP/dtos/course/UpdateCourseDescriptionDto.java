package com.stenaeke.TLP.dtos.course;

import com.stenaeke.TLP.domain.Course;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public final class UpdateCourseDescriptionDto implements UpdateCourseDto {
    @NotBlank
    private String description;

}
