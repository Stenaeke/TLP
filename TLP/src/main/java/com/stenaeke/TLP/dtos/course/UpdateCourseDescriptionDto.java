package com.stenaeke.TLP.dtos.course;

import com.stenaeke.TLP.domain.Course;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCourseDescriptionDto implements UpdateDto {
    @NotBlank
    private String description;

    @Override
    public void applyToCourse(Course course) {
        course.setDescription(description);
    }
}
